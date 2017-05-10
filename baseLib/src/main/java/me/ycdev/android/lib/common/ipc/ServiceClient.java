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
import android.support.annotation.WorkerThread;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import me.ycdev.android.lib.common.utils.LibLogger;
import me.ycdev.android.lib.common.utils.Preconditions;

public abstract class ServiceClient<IServiceInterface extends IInterface> {
    private static final String TAG = "ServiceClient";

    protected Context mAppContext;
    protected IServiceInterface mService;
    protected Handler mConnectHandler;

    private final AtomicBoolean mConnecting = new AtomicBoolean(false);

    protected ServiceClient(Context cxt) {
        mAppContext = cxt.getApplicationContext();
        mConnectHandler = new Handler(getConnectLooer());
    }

    /**
     * Get the looper used to connect/reconnect target Service.
     * By default, it's the main looper.
     */
    protected Looper getConnectLooer() {
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
        LibLogger.i(TAG, "Candidate services: %d", servicesList.size());
        Preconditions.checkArgument(servicesList.size() >= 1);
        ServiceInfo serviceInfo = servicesList.get(0).serviceInfo;
        for (ResolveInfo info : servicesList) {
            if ((info.serviceInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==
                    ApplicationInfo.FLAG_SYSTEM) {
                serviceInfo = info.serviceInfo; // search the system candidate
                LibLogger.i(TAG, "Service from system found and select it");
                break;
            }
        }
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    /**
     * Convert the IBinder object to interface.
     */
    protected abstract IServiceInterface asInterface(IBinder service);

    public void connect() {
        connectServiceIfNeeded();
    }

    private void connectServiceIfNeeded() {
        if (mService != null || mConnecting.get()) {
            LibLogger.d(TAG, "service is running or connecting");
            return;
        }

        mConnecting.set(true);

        Intent intent = getServiceIntent();
        List<ResolveInfo> servicesList = mAppContext.getPackageManager().queryIntentServices(intent, 0);
        if (servicesList == null || servicesList.size() == 0) {
            LibLogger.w(TAG, "no service component available, cannot connect");
            mConnecting.set(false);
            return;
        }
        // must set explicit component before bind/start service
        intent.setComponent(selectTargetService(servicesList));

        final ServiceConnection conn = new ServiceConnection() {
            private boolean mConnectLost = false;

            @Override
            public void onServiceConnected(ComponentName cn, IBinder service) {
                LibLogger.i(TAG, "service connected, cn: %s, mConnectLost: %s", cn, mConnectLost);
                if (!mConnectLost) {
                    mService = asInterface(service);
                } // else: waiting for reconnecting using new ServiceConnection object
            }

            @Override
            public void onServiceDisconnected(ComponentName cn) {
                LibLogger.i(TAG, "service disconnected, cn: %s, mConnectLost: %s", cn, mConnectLost);
                if (mConnectLost) {
                    return;
                }

                // Unbind the service and bind it again later
                mConnectLost = true;
                mService = null;
                mAppContext.unbindService(this);
                mConnecting.set(false);

                reconnectWithDelay(1000);
            }
        };

        LibLogger.i(TAG, "connecting service...");
        if (!mAppContext.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
            LibLogger.w(TAG, "cannot connect");
            mConnecting.set(false);
        }
    }

    private void reconnectWithDelay(long delayMillis) {
        mConnectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LibLogger.d(TAG, "delayed reconnect fires...");
                connect();
            }
        }, delayMillis);
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
        Preconditions.checkMainThread();
        if (mService != null) {
            return;
        }

        synchronized (mConnecting) {
            connect();
            long sleepTime = 50;
            long timeElapsed = 0;
            while (true) {
                LibLogger.d(TAG, "checking, service: %s, connecting: %s, time: %d/%d",
                        mService, mConnecting.get(), timeElapsed, timeoutMillis);
                if (mService != null || !mConnecting.get()) {
                    break;
                }
                if (timeoutMillis >= 0 && timeElapsed >= timeoutMillis) {
                    break;
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
            }
        }
    }

    public IServiceInterface getService() {
        return mService;
    }
}
