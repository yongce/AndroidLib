package me.ycdev.android.lib.common.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.RunningServiceInfo
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

@Suppress("unused")
object SystemServiceHelper {
    private const val TAG = "SystemServiceHelper"

    fun getActivityManager(context: Context): ActivityManager? {
        var am: ActivityManager? = null
        try {
            am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        } catch (e: Throwable) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get AM", e)
        }

        return am
    }

    fun getPackageManager(context: Context): PackageManager? {
        var pm: PackageManager? = null
        try {
            pm = context.packageManager
        } catch (e: Throwable) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get PM", e)
        }

        return pm
    }

    fun getRunningServices(am: ActivityManager, maxNum: Int): List<RunningServiceInfo> {
        var runServiceList: List<RunningServiceInfo>? = null
        try {
            @Suppress("DEPRECATION")
            runServiceList = am.getRunningServices(maxNum)
        } catch (e: Exception) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get running services", e)
        }

        if (runServiceList == null) {
            runServiceList = emptyList()
        }
        return runServiceList
    }

    fun getRunningAppProcesses(am: ActivityManager): List<RunningAppProcessInfo> {
        var runProcessList: List<RunningAppProcessInfo>? = null
        try {
            runProcessList = am.runningAppProcesses
        } catch (e: Exception) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get running processes", e)
        }

        if (runProcessList == null) {
            runProcessList = emptyList()
        }
        return runProcessList
    }

    fun getInstalledPackages(pm: PackageManager, flags: Int): List<PackageInfo> {
        var installedPackages: List<PackageInfo>? = null
        try {
            installedPackages = pm.getInstalledPackages(flags)
        } catch (e: Exception) {
            // Exception may be thrown on some devices
            LibLogger.w(TAG, "unexpected when get installed packages", e)
        }

        if (installedPackages == null) {
            installedPackages = emptyList()
        }
        return installedPackages
    }
}
