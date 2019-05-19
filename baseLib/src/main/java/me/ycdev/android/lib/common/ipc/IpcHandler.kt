package me.ycdev.android.lib.common.ipc

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

private fun createLooper(): Looper {
    val thread = HandlerThread("IpcHandler")
    thread.start()
    return thread.looper
}

@Suppress("unused")
object IpcHandler : Handler(createLooper())
