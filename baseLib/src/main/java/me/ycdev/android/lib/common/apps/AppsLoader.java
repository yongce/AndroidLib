package me.ycdev.android.lib.common.apps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.ycdev.android.lib.common.utils.MiscUtils;
import me.ycdev.android.lib.common.utils.PackageUtils;
import me.ycdev.android.lib.common.utils.StringUtils;

@SuppressWarnings("unused")
public class AppsLoader {
    private Context mAppContext;
    private PackageManager mPm;
    private String mMyselfPkgName;

    @SuppressLint("StaticFieldLeak")
    private static volatile AppsLoader sInstance;

    private AppsLoader(Context cxt) {
        mAppContext = cxt.getApplicationContext();
        mPm = cxt.getPackageManager();
        mMyselfPkgName = cxt.getPackageName();
    }

    public static AppsLoader getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (AppsLoader.class) {
                if (sInstance == null) {
                    sInstance = new AppsLoader(cxt);
                }
            }
        }
        return sInstance;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public List<AppInfo> loadInstalledApps(AppsLoadFilter filter, AppsLoadConfig config,
            AppsLoadListener listener) {
        HashMap<String, AppInfo> allApps = new HashMap<>();
        List<PackageInfo> installedApps = mPm.getInstalledPackages(0);
        int i = 0;
        int n = installedApps.size();
        for (PackageInfo pkgInfo : installedApps) {
            if (listener != null && listener.isCancelled()) {
                return new ArrayList<>(allApps.values());
            }

            AppInfo item = retrieveAppInfo(pkgInfo, filter, config);
            if (item != null) {
                allApps.put(item.pkgName, item);
            }
            if (listener != null) {
                i++;
                int percent = MiscUtils.calcProgressPercent(1, 50, i, n);
                listener.onProgressUpdated(percent, item);
            }
        }

        // The flag 'PackageManager.GET_UNINSTALLED_PACKAGES' may cause less information
        // about currently installed applications to be returned!
        // Such as, install time & update time, APK path, and so on.
        installedApps = mPm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        i = 0;
        n = installedApps.size();
        for (PackageInfo pkgInfo : installedApps) {
            if (listener != null && listener.isCancelled()) {
                return new ArrayList<>(allApps.values());
            }

            AppInfo item = null;
            if (!allApps.containsKey(pkgInfo.packageName)) {
                // unmounted app
                item = retrieveAppInfo(pkgInfo, filter, config);
                if (item != null) {
                    allApps.put(item.pkgName, item);
                }
            }
            if (listener != null) {
                i++;
                int percent = MiscUtils.calcProgressPercent(51, 100, i, n);
                listener.onProgressUpdated(percent, item);
            }
        }

        return new ArrayList<>(allApps.values());
    }

    private AppInfo retrieveAppInfo(PackageInfo pkgInfo, AppsLoadFilter filter,
            AppsLoadConfig config) {
        AppInfo item = new AppInfo();
        item.pkgName = pkgInfo.packageName;
        item.appUid = pkgInfo.applicationInfo.uid;
        item.sharedUid = pkgInfo.sharedUserId;

        int aiFlag = pkgInfo.applicationInfo.flags;
        item.isSysApp = (aiFlag & ApplicationInfo.FLAG_SYSTEM) != 0;
        item.isUpdatedSysApp = (aiFlag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;

        item.versionName = pkgInfo.versionName;
        item.versionCode = pkgInfo.versionCode;

        item.apkPath = pkgInfo.applicationInfo.sourceDir;
        item.isDisabled = !PackageUtils.isPkgEnabled(mAppContext, pkgInfo.packageName);
        // pkgInfo.applicationInfo.sourceDir may be null if the app is unmounted
        item.isUnmounted = pkgInfo.applicationInfo.sourceDir == null ||
                !new File(pkgInfo.applicationInfo.sourceDir).exists();
        item.installTime = pkgInfo.firstInstallTime;
        item.updateTime = pkgInfo.lastUpdateTime;

        if (filter.onlyMounted && item.isUnmounted) {
            return null;
        }
        if (filter.onlyEnabled && item.isDisabled) {
            return null;
        }
        if (!filter.includeSysApp && item.isSysApp) {
            if (!filter.includeUpdatedSysApp) {
                return null; // don't keep any system app and it's system app
            } else if (!item.isUpdatedSysApp) {
                return null; // only keep updated system app and it's not updated system app
            }
        }
        if (!filter.includeMyself && item.pkgName.equals(mMyselfPkgName)) {
            return null;
        }

        // do heavy loading
        if (config.loadLabel) {
            item.appName = StringUtils.trimPrefixSpaces(pkgInfo.applicationInfo.loadLabel(mPm).toString());
        }
        if (config.loadIcon) {
            item.appIcon = pkgInfo.applicationInfo.loadIcon(mPm);
        }

        return item;
    }
}
