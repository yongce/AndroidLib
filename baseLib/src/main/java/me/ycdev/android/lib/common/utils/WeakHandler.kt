package me.ycdev.android.lib.common.utils

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

@Suppress("unused")
class WeakHandler(msgHandler: Callback) : Handler() {
    private val targetHandler: WeakReference<Callback> = WeakReference(msgHandler)

    override fun handleMessage(msg: Message) {
        val realHandler = targetHandler.get()
        realHandler?.handleMessage(msg)
    }
}
