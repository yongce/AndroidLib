package me.ycdev.android.lib.common.utils;

public class Preconditions {
    public static void checkMainThread() {
        if (!ThreadUtils.isMainThread()) {
            throw new RuntimeException("Not in main thread");
        }
    }
}
