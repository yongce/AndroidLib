package me.ycdev.android.lib.common.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.view.inputmethod.InputMethodManager
import java.util.ArrayList

@Suppress("unused")
object PackageUtils {
    private const val TAG = "PackageUtils"

    /**
     * Value for [android.content.pm.ApplicationInfo.flags]: set to `true` if the application
     * is permitted to hold privileged permissions.
     */
    private const val FLAG_PRIVILEGED = 1 shl 3

    fun isPkgEnabled(cxt: Context, pkgName: String): Boolean {
        try {
            val state = cxt.packageManager.getApplicationEnabledSetting(pkgName)
            return state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } catch (e: IllegalArgumentException) {
            // the app had been uninstalled already
        }

        return true // by default
    }

    fun isPkgEnabled(appInfo: ApplicationInfo): Boolean {
        return appInfo.enabled
    }

    fun isPkgSystem(appInfo: ApplicationInfo): Boolean {
        return appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    /**
     * Check if an app is residing in "/system" (Android 4.3 and old versions)
     * or "/system/priv-app" (Android 4.4 and new versions) and has "signatureOrSystem" permission.
     */
    fun isPkgPrivileged(appInfo: ApplicationInfo): Boolean {
        return appInfo.flags and FLAG_PRIVILEGED != 0
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    fun isPkgStopped(appInfo: ApplicationInfo): Boolean {
        return appInfo.flags and ApplicationInfo.FLAG_STOPPED != 0
    }

    /**
     * @return An empty list if no launcher apps.
     */
    fun getLauncherApps(cxt: Context): List<String> {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val apps = cxt.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val pkgNames = hashSetOf<String>()
        for (info in apps) {
            pkgNames.add(info.activityInfo.packageName)
        }
        return pkgNames.toList()
    }

    /**
     * @return An empty list if no input method apps.
     */
    fun getInputMethodApps(cxt: Context): List<String> {
        val imm = cxt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            ?: return emptyList()

        val apps = imm.enabledInputMethodList
        val pkgNames = ArrayList<String>(apps.size)
        for (info in apps) {
            pkgNames.add(info.packageName)
        }
        return pkgNames
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getAllReceivers(
        cxt: Context,
        pkgName: String,
        onlyExported: Boolean
    ): Array<ActivityInfo> {
        try {
            val pm = cxt.packageManager
            val flags = PackageManager.GET_RECEIVERS or PackageManager.MATCH_DISABLED_COMPONENTS
            val pkgInfo = pm.getPackageInfo(pkgName, flags)
            if (onlyExported) {
                val tmpArray = arrayOfNulls<ActivityInfo>(pkgInfo.receivers.size)
                var size = 0
                for (item in pkgInfo.receivers) {
                    if (!item.exported) continue
                    tmpArray[size] = item
                    size++
                }
                @Suppress("UNCHECKED_CAST")
                return if (size == 0) emptyArray() else tmpArray.copyOf(size) as Array<ActivityInfo>
            } else {
                return pkgInfo.receivers
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LibLogger.w(TAG, "app not found", e)
        }

        return emptyArray()
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getAllServices(cxt: Context, pkgName: String, onlyExported: Boolean): Array<ServiceInfo> {
        try {
            val pm = cxt.packageManager
            val flags = PackageManager.GET_SERVICES or PackageManager.MATCH_DISABLED_COMPONENTS
            val pkgInfo = pm.getPackageInfo(pkgName, flags)
            if (onlyExported) {
                val tmpArray = arrayOfNulls<ServiceInfo>(pkgInfo.services.size)
                var size = 0
                for (item in pkgInfo.services) {
                    if (!item.exported) continue
                    tmpArray[size] = item
                    size++
                }
                @Suppress("UNCHECKED_CAST")
                return if (size == 0) emptyArray() else tmpArray.copyOf(size) as Array<ServiceInfo>
            } else {
                return pkgInfo.services
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LibLogger.w(TAG, "app not found", e)
        }

        return emptyArray()
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getAllActivities(
        cxt: Context,
        pkgName: String,
        onlyExported: Boolean
    ): Array<ActivityInfo> {
        try {
            val pm = cxt.packageManager
            val flags = PackageManager.GET_ACTIVITIES or PackageManager.MATCH_DISABLED_COMPONENTS
            val pkgInfo = pm.getPackageInfo(pkgName, flags)
            if (onlyExported) {
                val tmpArray = arrayOfNulls<ActivityInfo>(pkgInfo.activities.size)
                var size = 0
                for (item in pkgInfo.activities) {
                    if (!item.exported) continue
                    tmpArray[size] = item
                    size++
                }
                @Suppress("UNCHECKED_CAST")
                return if (size == 0) emptyArray() else tmpArray.copyOf(size) as Array<ActivityInfo>
            } else {
                return pkgInfo.activities
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LibLogger.w(TAG, "app not found", e)
        }

        return emptyArray()
    }
}
