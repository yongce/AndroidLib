package me.ycdev.android.lib.common.utils

import android.os.Looper

object ThreadUtils {
    val isMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    fun isThreadRunning(tid: Long): Boolean {
        val threadSet = Thread.getAllStackTraces().keys
        for (t in threadSet) {
            if (t.id == tid) {
                return true
            }
        }
        return false
    }

    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
