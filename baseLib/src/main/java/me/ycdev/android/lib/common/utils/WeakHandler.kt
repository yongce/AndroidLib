package me.ycdev.android.lib.common.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

@Suppress("unused")
class WeakHandler(looper: Looper, msgHandler: Callback) : Handler(looper) {
    private val targetHandler: WeakReference<Callback> = WeakReference(msgHandler)

    constructor(msgHandler: Callback) : this(Looper.myLooper()!!, msgHandler)

    override fun handleMessage(msg: Message) {
        val realHandler = targetHandler.get()
        realHandler?.handleMessage(msg)
    }
}
