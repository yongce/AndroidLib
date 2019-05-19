package me.ycdev.android.lib.common.utils

import java.lang.ref.WeakReference

import android.os.Handler
import android.os.Message

@Suppress("unused")
class WeakHandler(msgHandler: Callback) : Handler() {
    private val targetHandler: WeakReference<Callback> = WeakReference(msgHandler)

    override fun handleMessage(msg: Message) {
        val realHandler = targetHandler.get()
        realHandler?.handleMessage(msg)
    }
}
