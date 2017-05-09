package me.ycdev.android.lib.common.utils;

import android.os.Looper;

public class ThreadUtils {
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
