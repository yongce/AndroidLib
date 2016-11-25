package me.ycdev.android.lib.commonui.utils;

import android.support.annotation.RestrictTo;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestrictTo(RestrictTo.Scope.GROUP_ID)
public class LibLogger {
    public static final String  TAG = "AndroidUiLib";

    public static void v(String subTag, String msg) {
        if (LibConfigs.DISABLE_LOG) return;
        Log.v(TAG, getLogMsg(subTag, msg));
    }

    public static void d(String subTag, String msg) {
        if (LibConfigs.DISABLE_LOG) return;
        Log.d(TAG, getLogMsg(subTag, msg));
    }

    public static void i(String subTag, String msg) {
        if (LibConfigs.DISABLE_LOG) return;
        Log.i(TAG, getLogMsg(subTag, msg));
    }

    public static void w(String subTag, String msg) {
        if (LibConfigs.DISABLE_LOG) return;
        Log.w(TAG, getLogMsg(subTag, msg));
    }

    public static void w(String subTag, String msg, Throwable e) {
        if (LibConfigs.DISABLE_LOG) return;
        Log.w(TAG, getLogMsg(subTag, msg + " Exception: " + getExceptionMsg(e)));
    }

    public static void e(String subTag, String msg) {
        if (LibConfigs.DISABLE_LOG) return;
        Log.e(TAG, getLogMsg(subTag, msg));
    }

    public static void e(String subTag, String msg, Throwable e) {
        if (LibConfigs.DISABLE_LOG) return;
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
