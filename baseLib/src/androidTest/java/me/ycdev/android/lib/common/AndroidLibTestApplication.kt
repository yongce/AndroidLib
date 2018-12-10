package me.ycdev.android.lib.common

import android.app.Application

import me.ycdev.android.lib.common.utils.DebugUtils
import me.ycdev.android.lib.common.utils.LibLogger
import timber.log.Timber

class AndroidLibTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LibLogger.d(TAG, "onCreate")
        DebugUtils.enableStrictMode()
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        private const val TAG = "BaseLibTestApp"
    }
}
