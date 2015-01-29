package me.ycdev.android.lib.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

public class PackageUtils {
    /**
     * Value for {@link android.content.pm.ApplicationInfo#flags}: set to {@code true} if the application
     * is permitted to hold privileged permissions.
     *
     * {@hide}
     */
    private static final int FLAG_PRIVILEGED = 1<<30;

    public static boolean isPkgEnabled(@NonNull Context cxt, @NonNull String pkgName) {
        try {
            int state = cxt.getPackageManager().getApplicationEnabledSetting(pkgName);
            return (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        } catch (IllegalArgumentException e) {
            // the app had been uninstalled already
        }
        return true; // by default
    }

    public static boolean isPkgEnabled(@NonNull ApplicationInfo appInfo) {
        return appInfo.enabled;
    }

    public static boolean isPkgSystem(@NonNull ApplicationInfo appInfo) {
        return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    /**
     * Check if an app is residing in "/system" (Android 4.3 and old versions)
     * or "/system/priv-app" (Android 4.4 and new versions) and has "signatureOrSystem" permission.
     */
    public static boolean isPkgPrivileged(@NonNull ApplicationInfo appInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (appInfo.flags & FLAG_PRIVILEGED) != 0;
        } else {
            return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static boolean isPkgStopped(@NonNull ApplicationInfo appInfo) {
        return (appInfo.flags & ApplicationInfo.FLAG_STOPPED) != 0;
    }

    /**
     * @return An empty list if no launcher apps.
     */
    @NonNull
    public static List<String> getLauncherApps(@NonNull Context cxt) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> apps = cxt.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        List<String> pkgNames = new ArrayList<String>(apps.size());
        for (ResolveInfo info : apps) {
            pkgNames.add(info.activityInfo.packageName);
        }
        return pkgNames;
    }

    /**
     * @return An empty list if no input method apps.
     */
    @NonNull
    public static List<String> getInputMethodApps(@NonNull Context cxt) {
        InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> apps = imm.getEnabledInputMethodList();
        List<String> pkgNames = new ArrayList<String>(apps.size());
        for (InputMethodInfo info : apps) {
            pkgNames.add(info.getPackageName());
        }
        return pkgNames;
    }
}
