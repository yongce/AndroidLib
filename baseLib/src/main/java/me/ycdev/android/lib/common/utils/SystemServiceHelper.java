package me.ycdev.android.lib.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SystemServiceHelper {
    private static final String TAG = "SystemServiceHelper";

    @Nullable
    public static ActivityManager getActivityManager(@NonNull Context context) {
        ActivityManager am = null;
        try {
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        } catch (Throwable e) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get AM", e);
        }
        return am;
    }

    @Nullable
    public static PackageManager getPackageManager(@NonNull Context context) {
        PackageManager pm = null;
        try {
            pm = context.getPackageManager();
        } catch (Throwable e) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get PM", e);
        }
        return pm;
    }

    @NonNull
    public static List<ActivityManager.RunningServiceInfo> getRunningServices(
            @Nullable ActivityManager am, int maxNum) {
        List<ActivityManager.RunningServiceInfo> runServiceList = null;
        try {
            if (am != null) {
                runServiceList = am.getRunningServices(maxNum);
            }
        } catch (Exception e) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get running services", e);
        }
        if (runServiceList == null) {
            runServiceList = new ArrayList<>();
        }
        return runServiceList;
    }

    @NonNull
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(
            @Nullable ActivityManager am) {
        List<ActivityManager.RunningAppProcessInfo> runProcessList = null;
        try {
            if (am != null) {
                runProcessList = am.getRunningAppProcesses();
            }
        } catch (Exception e) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get running processes", e);
        }
        if (runProcessList == null) {
            runProcessList = new ArrayList<>();
        }
        return runProcessList;
    }

    @NonNull
    public static List<PackageInfo> getInstalledPackages(@Nullable PackageManager pm, int flags) {
        List<PackageInfo> installedPackages = null;
        try {
            if (pm != null) {
                installedPackages = pm.getInstalledPackages(flags);
            }
        } catch (Exception e) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get installed packages", e);
        }
        if (installedPackages == null) {
            installedPackages = new ArrayList<>();
        }
        return installedPackages;
    }

}
