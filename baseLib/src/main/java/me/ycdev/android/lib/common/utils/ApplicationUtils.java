package me.ycdev.android.lib.common.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ApplicationUtils {
    private static final String TAG = "ApplicationUtils";

    @SuppressLint("StaticFieldLeak")
    private static Application sApp;
    private static String sProcessName;

    /**
     * Must be called in Application#onCreate() ASAP.
     */
    public static void initApplication(Application app) {
        sApp = app;
        getCurrentProcessName(); // init process name in UI thread
    }

    public static Context getApplicationContext() {
        Preconditions.checkNotNull(sApp);
        return sApp;
    }

    public static String getCurrentProcessName() {
        Preconditions.checkNotNull(sApp);

        if (!TextUtils.isEmpty(sProcessName)) {
            return sProcessName;
        }

        // try AMS first
        int pid = Process.myPid();
        sProcessName = getProcessNameFromAMS(sApp, pid);
        if (!TextUtils.isEmpty(sProcessName)) {
            return sProcessName;
        }

        // try "/proc"
        sProcessName = getProcessNameFromProc(pid);
        return sProcessName;
    }

    @Nullable
    private static String getProcessNameFromAMS(Context cxt, int pid) {
        ActivityManager am = SystemServiceHelper.getActivityManager(cxt);
        List<ActivityManager.RunningAppProcessInfo> runningApps =
                SystemServiceHelper.getRunningAppProcesses(am);
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    @Nullable
    private static String getProcessNameFromProc(int pid) {
        String processName = null;
        try {
            String cmdlineFile = "/proc/" + pid + "/cmdline";
            processName = IoUtils.readAllLines(cmdlineFile);
        } catch (IOException e) {
            LibLogger.w(TAG, "failed to read process name from /proc for pid [%d]", pid);
        }
        if (processName != null) {
            processName = processName.trim();
        }
        return processName;
    }
}
