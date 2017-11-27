package me.ycdev.android.lib.common.utils;

import android.os.Handler;
import android.os.Looper;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MainHandler {
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static Handler getMainHandler() {
        return sHandler;
    }

    public static void post(Runnable r) {
        sHandler.post(r);
    }

    public static void postDelayed(Runnable r, long delayMs) {
        sHandler.postDelayed(r, delayMs);
    }

    public static void remove(Runnable r) {
        sHandler.removeCallbacks(r);
    }
}
