package me.ycdev.android.lib.common.wrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

/**
 * A wrapper class to avoid security issues when sending/receiving broadcast.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BroadcastHelper {
    public static final String PERM_INTERNAL_BROADCAST_SUFFIX = ".permission.INTERNAL";

    private BroadcastHelper() {
        // nothing to do
    }

    public static String getInternalBroadcastPerm(@NonNull Context cxt) {
        return cxt.getPackageName() + PERM_INTERNAL_BROADCAST_SUFFIX;
    }

    /**
     * Register a receiver for internal broadcast.
     */
    public static Intent registerForInternal(@NonNull Context cxt,
            @NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter) {
        String perm = cxt.getPackageName() + PERM_INTERNAL_BROADCAST_SUFFIX;
        return cxt.registerReceiver(receiver, filter, perm, null);
    }

    /**
     * Register a receiver for external broadcast (includes system broadcast).
     */
    public static Intent registerForExternal(@NonNull Context cxt,
            @NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter) {
        return cxt.registerReceiver(receiver, filter);
    }

    /**
     * Send a broadcast to internal receivers.
     */
    public static void sendToInternal(@NonNull Context cxt, @NonNull Intent intent) {
        String perm = cxt.getPackageName() + PERM_INTERNAL_BROADCAST_SUFFIX;
        intent.setPackage(cxt.getPackageName()); // only works on Android 4.0 and higher versions
        cxt.sendBroadcast(intent, perm);
    }

    /**
     * Send a broadcast to external receivers.
     */
    public static void sendToExternal(@NonNull Context cxt, @NonNull Intent intent,
            @NonNull String perm) {
        cxt.sendBroadcast(intent, perm);
    }

    /**
     * Send a broadcast to external receivers.
     */
    public static void sendToExternal(@NonNull Context cxt, @NonNull Intent intent) {
        cxt.sendBroadcast(intent);
    }

}
