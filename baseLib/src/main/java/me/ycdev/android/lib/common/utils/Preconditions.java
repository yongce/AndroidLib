package me.ycdev.android.lib.common.utils;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Preconditions {
    public static void checkMainThread() {
        if (!ThreadUtils.isMainThread()) {
            throw new RuntimeException("Not in main thread");
        }
    }

    public static void checkNonMainThread() {
        if (ThreadUtils.isMainThread()) {
            throw new RuntimeException("In main thread");
        }
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }
}
