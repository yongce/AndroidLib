package me.ycdev.android.arch.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import me.ycdev.android.lib.common.utils.FileLogger;
import me.ycdev.android.lib.common.utils.LibLogger;

/**
 * A wrapper class as logger.
 * <p>TODO To write custom lint rules to enforce only AppLogger used instead of android.util.Log.</p>
 */
public class AppLogger {
    private AppLogger() {
        // nothing to do
    }

    public static void enableJvmLogger() {
        LibLogger.enableJvmLogger();
    }

    public static void setFileLogger(FileLogger fileLogger) {
        LibLogger.setFileLogger(fileLogger);
    }

    public static void setDebug(boolean debug) {
        LibLogger.setDebug(debug);
    }

    public static boolean isDebug() {
        return LibLogger.isDebug();
    }

    public static void v(@NonNull String tag, @NonNull String msg, Object... args) {
        LibLogger.log(Log.VERBOSE, tag, msg, null, args);
    }

    public static void d(@NonNull String tag, @NonNull String msg, Object... args) {
        LibLogger.log(Log.DEBUG, tag, msg, null, args);
    }

    public static void i(@NonNull String tag, @NonNull String msg, Object... args) {
        LibLogger.log(Log.INFO, tag, msg, null, args);
    }

    public static void w(@NonNull String tag, @NonNull String msg, Object... args) {
        LibLogger.log(Log.WARN, tag, msg, null, args);
    }

    public static void w(@NonNull String tag, @NonNull String msg, @NonNull Throwable e,
            Object... args) {
        LibLogger.log(Log.WARN, tag, msg, e, args);
    }

    public static void w(@NonNull String tag, @NonNull Throwable e, Object... args) {
        LibLogger.log(Log.WARN, tag, null, e, args);
    }

    public static void e(@NonNull String tag, @NonNull String msg, Object... args) {
        LibLogger.log(Log.ERROR, tag, msg, null, args);
    }

    public static void e(@NonNull String tag, @NonNull String msg, @NonNull Throwable e,
            Object... args) {
        LibLogger.log(Log.ERROR, tag, msg, e, args);
    }

    public static void log(int level, @NonNull String tag, @Nullable String msg,
            @Nullable Throwable tr, Object... args) {
        LibLogger.log(level, tag, msg, tr, args);
    }

}
