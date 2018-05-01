package me.ycdev.android.lib.common.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.ycdev.android.lib.common.utils.Preconditions;
import me.ycdev.android.lib.common.utils.WeakListenerManager;
import timber.log.Timber;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ServiceConnector<IServiceInterface> {
    private static final String TAG = "ServiceConnector";

    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static final int MSG_RECONNECT = 1;
    private static final int MSG_NOTIFY_LISTENERS = 2;
    private static final int MSG_CONNECT_TIMEOUT_CHECK = 3;

    private static final long CONNECT_TIMEOUT_CHECK_INTERVAL = 5000; // 5s
    private static final long FORCE_REBIND_TIME = 30 * 1000; // 30 seconds

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_DISCONNECTED, STATE_CONNECTING, STATE_CONNECTED})
    public @interface ConnectState {}

    protected Context mAppContext;
    protected String mServiceName;
    protected IServiceInterface mService;

    protected WeakListenerManager<ConnectStateListener> mStateListeners = new WeakListenerManager<>();
    private final Object mConnectWaitLock = new Object();
    private AtomicInteger mState = new AtomicInteger(STATE_DISCONNECTED);
    private long mConnectStartTime;
    private ServiceConnection mServiceConnection;

    protected ServiceConnector(Context cxt, String serviceName) {
        mAppContext = cxt.getApplicationContext();
        mServiceName = serviceName;
    }

    /**
     * Get the looper used to connect/reconnect target Service.
     * By default, it's the main looper.
     */
    protected Looper getConnectLooper() {
        return Looper.getMainLooper();
    }

    /**
     * Get Intent to bind the target service.
     */
    @NonNull
    protected abstract Intent getServiceIntent();

    protected boolean validatePermission(String permission) {
        return true; // Skip to validate permission by default
    }

    /**
     * Sub class can rewrite the candidate services select logic.
     */
    @Nullable
    protected ComponentName selectTargetService(@NonNull List<ResolveInfo> servicesList) {
        Timber.tag(TAG).i("[%s] Candidate services: %d", mServiceName, servicesList.size());
        Preconditions.checkArgument(servicesList.size() >= 1);
        ServiceInfo serviceInfo = servicesList.get(0).serviceInfo;
        for (ResolveInfo info : servicesList) {
            if (!validatePermission(info.serviceInfo.permission)) {
                Timber.tag(TAG).w("Skip not-matched permission candidate: %s, perm: %s",
                        info.serviceInfo.name, info.serviceInfo.permission);
                continue;
            }
            if ((info.serviceInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==
                    ApplicationInfo.FLAG_SYSTEM) {
                serviceInfo = info.serviceInfo; // search the system candidate
                Timber.tag(TAG).i("[%s] Service from system found and select it", mServiceName);
                break;
            }
        }

        if (validatePermission(serviceInfo.permission)) {
            return new ComponentName(serviceInfo.packageName, serviceInfo.name);
        }
        return null;
    }

    /**
     * Convert the IBinder object to interface.
     */
    protected abstract IServiceInterface asInterface(IBinder service);

    /**
     * Add a connect state listener, using {@link WeakListenerManager} to manager listeners.
     * Callbacks will be invoked in {@link #getConnectLooper()} thread.
     */
    public void addListener(@NonNull ConnectStateListener listener) {
        mStateListeners.addListener(listener);
    }

    public void removeListener(@NonNull ConnectStateListener listener) {
        mStateListeners.removeListener(listener);
    }

    public boolean isServiceExist() {
        Intent intent = getServiceIntent();
        List<ResolveInfo> servicesList = mAppContext.getPackageManager().queryIntentServices(intent, 0);
        return servicesList != null && servicesList.size() > 0 && selectTargetService(servicesList) != null;
    }

    public void connect() {
        connectServiceIfNeeded(false);
    }

    public void disconnect() {
        Timber.tag(TAG).i("[%s] disconnect service...", mServiceName);
        mConnectHandler.removeMessages(MSG_CONNECT_TIMEOUT_CHECK);
        mConnectHandler.removeMessages(MSG_RECONNECT);
        mService = null;
        if (mServiceConnection != null) {
            mAppContext.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        updateConnectState(STATE_DISCONNECTED);
    }

    private void connectServiceIfNeeded(boolean rebind) {
        if (mService != null) {
            Timber.tag(TAG).d("[%s] service is connected", mServiceName);
            return;
        }
        if (!rebind) {
            if (!mState.compareAndSet(STATE_DISCONNECTED, STATE_CONNECTING)) {
                Timber.tag(TAG).d("[%s] Service is under connecting", mServiceName);
                return;
            }
            updateConnectState(STATE_CONNECTING);
        }
        mConnectStartTime = SystemClock.elapsedRealtime();

        Intent intent = getServiceIntent();
        List<ResolveInfo> servicesList = mAppContext.getPackageManager().queryIntentServices(intent, 0);
        if (servicesList == null || servicesList.size() == 0) {
            Timber.tag(TAG).w("[%s] no service component available, cannot connect", mServiceName);
            updateConnectState(STATE_DISCONNECTED);
            return;
        }
        ComponentName candidateService = selectTargetService(servicesList);
        if (candidateService == null) {
            Timber.tag(TAG).w("[%s] no expected service component found, cannot connect", mServiceName);
            updateConnectState(STATE_DISCONNECTED);
            return;
        }
        // must set explicit component before bind/start service
        intent.setComponent(candidateService);

        mServiceConnection = new ServiceConnection() {
            private boolean mConnectLost = false;

            @Override
            public void onServiceConnected(ComponentName cn, IBinder service) {
                Timber.tag(TAG).i("[%s] service connected, cn: %s, mConnectLost: %s",
                        mServiceName,  cn, mConnectLost);
                if (!mConnectLost) {
                    // update 'mService' first, and then update the connect state and notify
                    mService = asInterface(service);
                    mConnectHandler.removeMessages(MSG_CONNECT_TIMEOUT_CHECK);
                    updateConnectState(STATE_CONNECTED);
                } // else: waiting for reconnecting using new ServiceConnection object
            }

            @Override
            public void onServiceDisconnected(ComponentName cn) {
                Timber.tag(TAG).i("[%s] service disconnected, cn: %s, mConnectLost: %s",
                        mServiceName, cn, mConnectLost);
                if (mConnectLost) {
                    return;
                }

                // Unbind the service and bind it again later
                mConnectLost = true;
                disconnect();

                mConnectHandler.sendEmptyMessageDelayed(MSG_RECONNECT, 1000);
            }
        };

        Timber.tag(TAG).i("[%s] connecting service...", mServiceName);
        if (!mAppContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
            Timber.tag(TAG).w("[%s] cannot connect", mServiceName);
            updateConnectState(STATE_DISCONNECTED);
        } else {
            mConnectHandler.removeMessages(MSG_CONNECT_TIMEOUT_CHECK);
            mConnectHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT_CHECK,
                    CONNECT_TIMEOUT_CHECK_INTERVAL);
        }
    }

    private void updateConnectState(@ConnectState int newState) {
        if (newState != STATE_CONNECTING) {
            mState.set(newState);
        }
        mConnectHandler.obtainMessage(MSG_NOTIFY_LISTENERS, newState, 0).sendToTarget();
    }

    /**
     * Waiting for the service connected.
     */
    @WorkerThread
    public void waitForConnected() {
        waitForConnected(-1);
    }

    /**
     * Waiting for the service connected with timeout.
     *
     * @param timeoutMillis Timeout in milliseconds to wait for the service connected.
     *                      0 means no waiting and -1 means no timeout.
     */
    @WorkerThread
    public void waitForConnected(long timeoutMillis) {
        Preconditions.checkNonMainThread();
        if (mService != null) {
            Timber.tag(TAG).d("[%s] already connected", mServiceName);
            return;
        }

        synchronized (mConnectWaitLock) {
            connect();
            long sleepTime = 50;
            long timeElapsed = 0;
            while (true) {
                int connectState = mState.get();
                Timber.tag(TAG).d("[%s] checking, service: %s, state: %d, time: %d/%d",
                        mServiceName, mService, connectState, timeElapsed, timeoutMillis);
                if (connectState == STATE_CONNECTED || connectState == STATE_DISCONNECTED) {
                    break;
                }
                if (timeoutMillis >= 0 && timeElapsed >= timeoutMillis) {
                    break;
                }

                connect();
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Timber.tag(TAG).w(e, "interrupted");
                    break;
                }

                timeElapsed = timeElapsed + sleepTime;
                sleepTime = sleepTime * 2;
                if (sleepTime > 1000) {
                    sleepTime = 1000;
                }
            }
        }
    }

    public IServiceInterface getService() {
        return mService;
    }

    @ConnectState
    public int getConnectState() {
        //noinspection WrongConstant
        return mState.get();
    }

    public static String strConnectState(int state) {
        if (state == STATE_DISCONNECTED) {
            return "disconnected";
        } else if (state == STATE_CONNECTING) {
            return "connecting";
        } else if (state == STATE_CONNECTED) {
            return "connected";
        } else {
            return "unknown";
        }
    }

    private Handler mConnectHandler = new Handler(getConnectLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECONNECT: {
                    Timber.tag(TAG).d("[%s] delayed reconnect fires...", mServiceName);
                    connect();
                    break;
                }

                case MSG_NOTIFY_LISTENERS: {
                    final @ConnectState int newState = msg.arg1;
                    Timber.tag(TAG).d("State changed: %s", strConnectState(newState));
                    mStateListeners.notifyListeners(listener -> listener.onStateChanged(newState));
                    break;
                }

                case MSG_CONNECT_TIMEOUT_CHECK: {
                    Timber.tag(TAG).d("checking connect timeout");
                    int curState = mState.get();
                    if (SystemClock.elapsedRealtime() - mConnectStartTime >= FORCE_REBIND_TIME) {
                        Timber.tag(TAG).d("[%s] connect timeout, state: %s",
                                mServiceName, curState);
                        if (curState == STATE_CONNECTING) {
                            // force to rebind the service
                            connectServiceIfNeeded(true);
                        }
                    } else {
                        if (curState == STATE_CONNECTING) {
                            mConnectHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT_CHECK,
                                    CONNECT_TIMEOUT_CHECK_INTERVAL);
                        }
                    }
                    break;
                }
            }
        }
    };
}
