package me.ycdev.android.arch.demo.wrapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import me.ycdev.android.lib.common.wrapper.BroadcastHelper

object BroadcastHelperLintCase {
    private class Foo {
        fun registerReceiver() { // lint good
        }

        fun sendBroadcast() { // lint good
        }
    }

    fun registerReceiver() { // lint good
        Foo().registerReceiver()
    }

    fun sendBroadcast() { // lint good
        Foo().sendBroadcast()
    }

    fun registerGood(cxt: Context, receiver: BroadcastReceiver, filter: IntentFilter): Intent? {
        return BroadcastHelper.registerForInternal(cxt, receiver, filter) // lint good
    }

    fun sendToInternalGood(cxt: Context, intent: Intent) {
        BroadcastHelper.sendToInternal(cxt, intent) // lint good
    }

    fun sendToExternalGood(cxt: Context, intent: Intent, perm: String) {
        BroadcastHelper.sendToExternal(cxt, intent, perm) // lint good
    }

    fun sendToExternal(cxt: Context, intent: Intent) {
        BroadcastHelper.sendToExternal(cxt, intent) // lint good
    }

    fun registerViolation(
        cxt: Context,
        receiver: BroadcastReceiver,
        filter: IntentFilter
    ): Intent? {
        return cxt.registerReceiver(receiver, filter) // lint violation
    }

    fun registerViolation2(
        cxt: Context,
        receiver: BroadcastReceiver,
        filter: IntentFilter
    ): Intent? {
        return cxt.registerReceiver(receiver, filter, null, null) // lint violation
    }

    fun sendViolation(cxt: Context, intent: Intent, perm: String) {
        cxt.sendBroadcast(intent, perm) // lint violation
    }

    fun sendViolation2(cxt: Context, intent: Intent) {
        cxt.sendBroadcast(intent) // lint violation
    }
}
