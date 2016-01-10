package me.ycdev.android.arch.demo.wrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import me.ycdev.android.arch.wrapper.BroadcastHelper;

public class BroadcastHelperLintCase {
    public static Intent registerGood(Context cxt, BroadcastReceiver receiver, IntentFilter filter) {
        return BroadcastHelper.registerForInternal(cxt, receiver, filter); // lint good
    }

    public static void sendToInternalGood(Context cxt, Intent intent) {
        BroadcastHelper.sendToInternal(cxt, intent); // lint good
    }

    public static void sendToExternalGood(Context cxt, Intent intent, String perm) {
        BroadcastHelper.sendToExternal(cxt, intent, perm); // lint good
    }

    public static void sendToExternal(Context cxt, Intent intent) {
        BroadcastHelper.sendToExternal(cxt, intent); // lint good
    }

    public static Intent registerViolation(Context cxt, BroadcastReceiver receiver, IntentFilter filter) {
        return cxt.registerReceiver(receiver, filter); // lint violation
    }

    public static Intent registerViolation2(Context cxt, BroadcastReceiver receiver, IntentFilter filter) {
        return cxt.registerReceiver(receiver, filter, null, null); // lint violation
    }

    public static void sendViolation(Context cxt, Intent intent, String perm) {
        cxt.sendBroadcast(intent, perm); // lint violation
    }

    public static void sendViolation2(Context cxt, Intent intent) {
        cxt.sendBroadcast(intent); // lint violation
    }

}
