package me.ycdev.android.lib.common.wrapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * A wrapper class to avoid security issues when sending/receiving broadcast.
 */
@Suppress("unused")
object BroadcastHelper {
    private const val PERM_INTERNAL_BROADCAST_SUFFIX = ".permission.INTERNAL"

    fun getInternalBroadcastPerm(cxt: Context): String {
        return cxt.packageName + PERM_INTERNAL_BROADCAST_SUFFIX
    }

    /**
     * Register a receiver for internal broadcast.
     */
    fun registerForInternal(
        cxt: Context,
        receiver: BroadcastReceiver,
        filter: IntentFilter
    ): Intent? {
        val perm = cxt.packageName + PERM_INTERNAL_BROADCAST_SUFFIX
        return cxt.registerReceiver(receiver, filter, perm, null)
    }

    /**
     * Register a receiver for external broadcast (includes system broadcast).
     */
    fun registerForExternal(
        cxt: Context,
        receiver: BroadcastReceiver,
        filter: IntentFilter
    ): Intent? {
        return cxt.registerReceiver(receiver, filter)
    }

    /**
     * Send a broadcast to internal receivers.
     */
    fun sendToInternal(cxt: Context, intent: Intent) {
        val perm = cxt.packageName + PERM_INTERNAL_BROADCAST_SUFFIX
        intent.setPackage(cxt.packageName) // only works on Android 4.0 and higher versions
        cxt.sendBroadcast(intent, perm)
    }

    /**
     * Send a broadcast to external receivers.
     */
    fun sendToExternal(
        cxt: Context,
        intent: Intent,
        perm: String?
    ) {
        cxt.sendBroadcast(intent, perm)
    }

    /**
     * Send a broadcast to external receivers.
     */
    fun sendToExternal(cxt: Context, intent: Intent) {
        cxt.sendBroadcast(intent)
    }
}
