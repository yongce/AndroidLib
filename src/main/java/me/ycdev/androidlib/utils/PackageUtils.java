package me.ycdev.androidlib.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageUtils {
    public static boolean isPkgEnabled(Context cxt, String pkgName) {
        try {
            int state = cxt.getPackageManager().getApplicationEnabledSetting(pkgName);
            return (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        } catch (IllegalArgumentException e) {
            // the app had been uninstalled already
        }
        return true; // by default
    }

    public static boolean isPkgEnabled(ApplicationInfo appInfo) {
        return appInfo.enabled;
    }

    public static boolean isPkgSystem(ApplicationInfo appInfo) {
        return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
