package me.ycdev.android.lib.common.utils;

import android.os.Looper;

import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ThreadUtils {
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isThreadRunning(long tid) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet) {
            if (t.getId() == tid) {
                return true;
            }
        }
        return false;
    }
}
