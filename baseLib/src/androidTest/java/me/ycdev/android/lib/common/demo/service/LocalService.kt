package me.ycdev.android.lib.common.demo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import androidx.annotation.Nullable
import timber.log.Timber

class LocalService : Service() {

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d("onDestroy")
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return BinderServer()
    }

    private inner class BinderServer : IDemoService.Stub() {
        @Throws(RemoteException::class)
        override fun who(): String {
            return "LocalService"
        }

        @Throws(RemoteException::class)
        override fun sayHello(gift: String) {
            Timber.tag(TAG).d("Received gift from someone: %s", gift)
        }
    }

    companion object {
        private const val TAG = "LocalService"
    }
}
