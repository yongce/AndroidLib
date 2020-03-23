package me.ycdev.android.lib.common.apps

import android.graphics.drawable.Drawable

import java.text.Collator
import java.util.Comparator

import me.ycdev.android.lib.common.utils.DateTimeUtils

data class AppInfo(val pkgName: String) {
    var appUid: Int = 0
    var sharedUid: String? = null
    var appName: String? = null
    var appIcon: Drawable? = null
    var versionName: String? = null
    var versionCode: Int = 0
    var apkPath: String? = null
    var installTime: Long = 0
    var updateTime: Long = 0
    var isSysApp: Boolean = false
    var isUpdatedSysApp: Boolean = false
    var isDisabled: Boolean = false
    var isUnmounted: Boolean = false
    var isSelected: Boolean = false
    var targetSdkVersion: Int = 0
    var minSdkVersion: Int = 0

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("AppInfo[")
        sb.append("pkgName: ").append(pkgName)
        sb.append(", appUid: ").append(appUid)
        sb.append(", sharedUid: ").append(sharedUid)
        sb.append(", appName: ").append(appName)
        sb.append(", versionName: ").append(versionName)
        sb.append(", versionCode: ").append(versionCode)
        sb.append(", apkPath: ").append(apkPath)
        sb.append(", installTime: ").append(DateTimeUtils.getReadableTimeStamp(installTime))
        sb.append(", updateTime: ").append(DateTimeUtils.getReadableTimeStamp(updateTime))
        sb.append(", isSysApp: ").append(isSysApp)
        sb.append(", isUpdatedSysApp: ").append(isUpdatedSysApp)
        sb.append(", isDisabled: ").append(isDisabled)
        sb.append(", isUnmounted: ").append(isUnmounted)
        sb.append(", isSelected: ").append(isSelected)
        sb.append("]")
        return sb.toString()
    }

    class AppNameComparator : Comparator<AppInfo> {
        private val collator = Collator.getInstance()

        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return collator.compare(lhs.appName, rhs.appName)
        }
    }

    class PkgNameComparator : Comparator<AppInfo> {
        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return lhs.pkgName.compareTo(rhs.pkgName)
        }
    }

    class UidComparator : Comparator<AppInfo> {
        private val pkgNameComparator = PkgNameComparator()

        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return when {
                lhs.appUid < rhs.appUid -> -1
                lhs.appUid > rhs.appUid -> 1
                else -> pkgNameComparator.compare(lhs, rhs)
            }
        }
    }

    class InstallTimeComparator : Comparator<AppInfo> {
        private val pkgNameComparator = PkgNameComparator()

        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return when {
                lhs.installTime < rhs.installTime -> 1
                lhs.installTime > rhs.installTime -> -1
                else -> pkgNameComparator.compare(lhs, rhs)
            }
        }
    }

    class UpdateTimeComparator : Comparator<AppInfo> {
        private val pkgNameComparator = PkgNameComparator()

        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return when {
                lhs.updateTime < rhs.updateTime -> 1
                lhs.updateTime > rhs.updateTime -> -1
                else -> pkgNameComparator.compare(lhs, rhs)
            }
        }
    }

    class TargetSdkComparator : Comparator<AppInfo> {
        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return lhs.targetSdkVersion - rhs.targetSdkVersion
        }
    }

    class MinSdkComparator : Comparator<AppInfo> {
        override fun compare(lhs: AppInfo, rhs: AppInfo): Int {
            return lhs.minSdkVersion - rhs.minSdkVersion
        }
    }
}
