package me.ycdev.android.lib.common.utils;

import android.os.Looper;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ThreadUtils {
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
