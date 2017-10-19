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
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.ycdev.android.lib.common.utils.LibLogger;
import me.ycdev.android.lib.common.utils.Preconditions;
import me.ycdev.android.lib.common.utils.WeakListenerManager;
import me.ycdev.android.lib.common.utils.WeakListenerManager.NotifyAction;

public abstract class ServiceClient<IServiceInterface extends IInterface> {
    private static final String TAG = "ServiceClient";

    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static final int MSG_RECONNECT = 1;
    private static final int MSG_NOTIFY_LISTENERS = 2;

    private static final long FORCE_REBIND_TIME = 60 * 1000; // 1 minute

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_DISCONNECTED, STATE_CONNECTING, STATE_CONNECTED})
    public @interface ConnectState {}

    protected Context mAppContext;
    protected String mServiceName;
    protected IServiceInterface mService;

    protected WeakListenerManager<ConnectStateListener> mStateListeners = new WeakListenerManager<>();
    private final Object mConnectWaitLock = new Object();
    private AtomicInteger mState = new AtomicInteger(STATE_DISCONNECTED);

    protected ServiceClient(Context cxt, String serviceName) {
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
    protected abstract Intent getServiceIntent();

    /**
     * Sub class can rewrite the candidate services select logic.
     */
    protected ComponentName selectTargetService(List<ResolveInfo> servicesList) {
        LibLogger.i(TAG, "[%s] Candidate services: %d", mServiceName, servicesList.size());
        Preconditions.checkArgument(servicesList.size() >= 1);
        ServiceInfo serviceInfo = servicesList.get(0).serviceInfo;
        for (ResolveInfo info : servicesList) {
            if ((info.serviceInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==
                    ApplicationInfo.FLAG_SYSTEM) {
                serviceInfo = info.serviceInfo; // search the system candidate
                LibLogger.i(TAG, "[%s] Service from system found and select it", mServiceName);
                break;
            }
        }
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
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

    public void connect() {
        connectServiceIfNeeded();
    }

    private void connectServiceIfNeeded() {
        if (mService != null) {
            LibLogger.d(TAG, "[%s] service is connected", mServiceName);
            return;
        }
        if (!mState.compareAndSet(STATE_DISCONNECTED, STATE_CONNECTING)) {
            LibLogger.d(TAG, "[%s] Service is under connecting", mServiceName);
            return;
        }
        updateConnectState(STATE_CONNECTING);

        Intent intent = getServiceIntent();
        List<ResolveInfo> servicesList = mAppContext.getPackageManager().queryIntentServices(intent, 0);
        if (servicesList == null || servicesList.size() == 0) {
            LibLogger.w(TAG, "[%s] no service component available, cannot connect", mServiceName);
            updateConnectState(STATE_DISCONNECTED);
            return;
        }
        // must set explicit component before bind/start service
        intent.setComponent(selectTargetService(servicesList));

        final ServiceConnection conn = new ServiceConnection() {
            private boolean mConnectLost = false;

            @Override
            public void onServiceConnected(ComponentName cn, IBinder service) {
                LibLogger.i(TAG, "[%s] service connected, cn: %s, mConnectLost: %s",
                        mServiceName,  cn, mConnectLost);
                if (!mConnectLost) {
                    mService = asInterface(service);
                    updateConnectState(STATE_CONNECTED);
                } // else: waiting for reconnecting using new ServiceConnection object
            }

            @Override
            public void onServiceDisconnected(ComponentName cn) {
                LibLogger.i(TAG, "[%s] service disconnected, cn: %s, mConnectLost: %s",
                        mServiceName, cn, mConnectLost);
                if (mConnectLost) {
                    return;
                }

                // Unbind the service and bind it again later
                mConnectLost = true;
                mService = null;
                mAppContext.unbindService(this);
                updateConnectState(STATE_DISCONNECTED);

                mConnectHandler.sendEmptyMessageDelayed(MSG_RECONNECT, 1000);
            }
        };

        LibLogger.i(TAG, "[%s] connecting service...", mServiceName);
        if (!mAppContext.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
            LibLogger.w(TAG, "[%s] cannot connect", mServiceName);
            updateConnectState(STATE_DISCONNECTED);
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
            LibLogger.d(TAG, "[%s] already connected", mServiceName);
            return;
        }

        synchronized (mConnectWaitLock) {
            connect();
            long sleepTime = 50;
            long timeElapsed = 0;
            while (true) {
                LibLogger.d(TAG, "[%s] checking, service: %s, state: %d, time: %d/%d",
                        mServiceName, mService, mState.get(), timeElapsed, timeoutMillis);
                if (mService != null || mState.get() == STATE_DISCONNECTED) {
                    break;
                }
                if (timeoutMillis >= 0 && timeElapsed >= timeoutMillis) {
                    break;
                }
                if (timeElapsed >= FORCE_REBIND_TIME) {
                    // if the state is in "connecting" always, try to rebind
                    LibLogger.d(TAG, "[%s] force to rebind", mServiceName);
                    mState.set(STATE_DISCONNECTED);
                }

                connect();
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    LibLogger.w(TAG, "interrupted", e);
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

    private Handler mConnectHandler = new Handler(getConnectLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECONNECT: {
                    LibLogger.d(TAG, "[%s] delayed reconnect fires...", mServiceName);
                    connect();
                    break;
                }

                case MSG_NOTIFY_LISTENERS: {
                    final @ConnectState int newState = msg.arg1;
                    mStateListeners.notifyListeners(new NotifyAction<ConnectStateListener>() {
                        @Override
                        public void notify(ConnectStateListener listener) {
                            listener.onStateChanged(newState);
                        }
                    });
                    break;
                }
            }
        }
    };
}
