package me.ycdev.android.lib.common.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class LocalService extends Service {
    private static final String TAG = "LocalService";

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag(TAG).d("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BinderServer();
    }

    private class BinderServer extends IDemoService.Stub {
        @Override
        public String who() throws RemoteException {
            return "LocalService";
        }

        @Override
        public void sayHello(String gift) throws RemoteException {
            Timber.tag(TAG).d("Received gift from someone: %s", gift);
        }
    }
}
