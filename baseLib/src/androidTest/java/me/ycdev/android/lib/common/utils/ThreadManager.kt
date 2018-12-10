package me.ycdev.android.lib.common.utils

import android.os.HandlerThread
import android.os.Looper

class ThreadManager {

    private var mLocalServiceRequestIpcThread: HandlerThread? = null
    private var mRemoteServiceRequestIpcThread: HandlerThread? = null

    @Synchronized
    fun localServiceRequestIpcLooper(): Looper {
        if (mLocalServiceRequestIpcThread == null) {
            mLocalServiceRequestIpcThread = HandlerThread("LocalService.client")
            mLocalServiceRequestIpcThread!!.start()
        }
        return mLocalServiceRequestIpcThread!!.looper
    }

    @Synchronized
    fun remoteServiceRequestIpcLooper(): Looper {
        if (mRemoteServiceRequestIpcThread == null) {
            mRemoteServiceRequestIpcThread = HandlerThread("RemoteService.client")
            mRemoteServiceRequestIpcThread!!.start()
        }
        return mRemoteServiceRequestIpcThread!!.looper
    }

    companion object {
        val instance = ThreadManager()
    }
}
