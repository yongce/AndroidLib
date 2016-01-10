package me.ycdev.android.arch.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import me.ycdev.android.arch.BuildConfig;

/**
 * A wrapper class as logger.
 * <p>TODO To write custom lint rules to enforce only AppLogger used instead of android.util.Log.</p>
 */
public class AppLogger {
    public static final String TAG = BuildConfig.LOG_TAG;

    private AppLogger() {
        // nothing to do
    }

    public static void v(String subTag, String msg) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.v(TAG, getLogMsg(subTag, msg));
    }

    public static void d(String subTag, String msg) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.d(TAG, getLogMsg(subTag, msg));
    }

    public static void i(String subTag, String msg) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.i(TAG, getLogMsg(subTag, msg));
    }

    public static void w(String subTag, String msg) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.w(TAG, getLogMsg(subTag, msg));
    }

    public static void w(String subTag, String msg, Throwable e) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.w(TAG, getLogMsg(subTag, msg + " Exception: " + getExceptionMsg(e)));
    }

    public static void e(String subTag, String msg) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.e(TAG, getLogMsg(subTag, msg));
    }

    public static void e(String subTag, String msg, Throwable e) {
        if (AppConfigs.DISABLE_LOG) return;
        Log.e(TAG, getLogMsg(subTag, msg + " Exception: " + getExceptionMsg(e)));
    }

    private static String getLogMsg(String subTag, String msg) {
        return "[" + subTag + "] " + msg;
    }

    private static String getExceptionMsg(Throwable e) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
}
