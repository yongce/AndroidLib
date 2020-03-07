package me.ycdev.android.lib.common.apps

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import me.ycdev.android.lib.common.pattern.SingletonHolderP1
import me.ycdev.android.lib.common.utils.MiscUtils
import me.ycdev.android.lib.common.utils.PackageUtils
import me.ycdev.android.lib.common.utils.StringUtils
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class AppsLoader private constructor(cxt: Context) {
    private val appContext: Context = cxt.applicationContext
    private val pm: PackageManager = cxt.packageManager
    private val myselfPkgName: String = cxt.packageName

    @TargetApi(Build.VERSION_CODES.N)
    fun loadInstalledApps(
        filter: AppsLoadFilter,
        config: AppsLoadConfig,
        listener: AppsLoadListener?
    ): List<AppInfo> {
        val allApps = HashMap<String, AppInfo>()
        var installedApps = pm.getInstalledPackages(0)
        var i = 0
        var n = installedApps.size
        for (pkgInfo in installedApps) {
            if (listener != null && listener.isCancelled()) {
                return ArrayList(allApps.values)
            }

            val item = retrieveAppInfo(pkgInfo, filter, config)
            if (item != null) {
                allApps[item.pkgName] = item

                if (listener != null) {
                    i++
                    val percent = MiscUtils.calcProgressPercent(1, 50, i, n)
                    listener.onProgressUpdated(percent, item)
                }
            }
        }

        // The flag 'PackageManager.GET_UNINSTALLED_PACKAGES' may cause less information
        // about currently installed applications to be returned!
        // Such as, install time & update time, APK path, and so on.
        installedApps = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        i = 0
        n = installedApps.size
        for (pkgInfo in installedApps) {
            if (listener != null && listener.isCancelled()) {
                return ArrayList(allApps.values)
            }

            var item: AppInfo? = null
            if (!allApps.containsKey(pkgInfo.packageName)) {
                // unmounted app
                item = retrieveAppInfo(pkgInfo, filter, config)
                if (item != null) {
                    allApps[item.pkgName] = item
                }
            }
            if (listener != null && item != null) {
                i++
                val percent = MiscUtils.calcProgressPercent(51, 100, i, n)
                listener.onProgressUpdated(percent, item)
            }
        }

        return ArrayList(allApps.values)
    }

    private fun retrieveAppInfo(
        pkgInfo: PackageInfo,
        filter: AppsLoadFilter,
        config: AppsLoadConfig
    ): AppInfo? {
        val item = AppInfo(pkgInfo.packageName)
        item.appUid = pkgInfo.applicationInfo.uid
        item.sharedUid = pkgInfo.sharedUserId

        val aiFlag = pkgInfo.applicationInfo.flags
        item.isSysApp = aiFlag and ApplicationInfo.FLAG_SYSTEM != 0
        item.isUpdatedSysApp = aiFlag and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0

        item.versionName = pkgInfo.versionName
        item.versionCode = pkgInfo.versionCode

        item.apkPath = pkgInfo.applicationInfo.sourceDir
        item.isDisabled = !PackageUtils.isPkgEnabled(appContext, pkgInfo.packageName)
        // pkgInfo.applicationInfo.sourceDir may be null if the app is unmounted
        item.isUnmounted =
            pkgInfo.applicationInfo.sourceDir == null || !File(pkgInfo.applicationInfo.sourceDir).exists()
        item.installTime = pkgInfo.firstInstallTime
        item.updateTime = pkgInfo.lastUpdateTime

        if (filter.onlyMounted && item.isUnmounted) {
            return null
        }
        if (filter.onlyEnabled && item.isDisabled) {
            return null
        }
        if (!filter.includeSysApp && item.isSysApp) {
            if (!filter.includeUpdatedSysApp) {
                return null // don't keep any system app and it's system app
            } else if (!item.isUpdatedSysApp) {
                return null // only keep updated system app and it's not updated system app
            }
        }
        if (!filter.includeMyself && item.pkgName == myselfPkgName) {
            return null
        }

        // do heavy loading
        if (config.loadLabel) {
            item.appName =
                StringUtils.trimPrefixSpaces(pkgInfo.applicationInfo.loadLabel(pm).toString())
        }
        if (config.loadIcon) {
            item.appIcon = pkgInfo.applicationInfo.loadIcon(pm)
        }

        return item
    }

    companion object : SingletonHolderP1<AppsLoader, Context>(::AppsLoader)
}
