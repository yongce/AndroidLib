package me.ycdev.android.lib.common.apps;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.text.Collator;
import java.util.Comparator;

import me.ycdev.android.lib.common.utils.DateTimeUtils;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AppInfo {
    public String pkgName;
    public int appUid;
    public String sharedUid;
    @Nullable
    public String appName;
    @Nullable
    public Drawable appIcon;
    @Nullable
    public String versionName;
    public int versionCode;
    @Nullable
    public String apkPath;
    public long installTime;
    public long updateTime;
    public boolean isSysApp;
    public boolean isUpdatedSysApp;
    public boolean isDisabled;
    public boolean isUnmounted;
    public boolean isSelected;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AppInfo[");
        sb.append("pkgName: ").append(pkgName);
        sb.append(", appUid: ").append(appUid);
        sb.append(", sharedUid: ").append(sharedUid);
        sb.append(", appName: ").append(appName);
        sb.append(", versionName: ").append(versionName);
        sb.append(", versionCode: ").append(versionCode);
        sb.append(", apkPath: ").append(apkPath);
        sb.append(", installTime: ").append(DateTimeUtils.getReadableTimeStamp(installTime));
        sb.append(", updateTime: ").append(DateTimeUtils.getReadableTimeStamp(updateTime));
        sb.append(", isSysApp: ").append(isSysApp);
        sb.append(", isUpdatedSysApp: ").append(isUpdatedSysApp);
        sb.append(", isDisabled: ").append(isDisabled);
        sb.append(", isUnmounted: ").append(isUnmounted);
        sb.append(", isSelected: ").append(isSelected);
        sb.append("]");
        return sb.toString();
    }

    public static class AppNameComparator implements Comparator<AppInfo> {
        private Collator mCollator = Collator.getInstance();

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return mCollator.compare(lhs.appName, rhs.appName);
        }
    }

    public static class PkgNameComparator implements Comparator<AppInfo> {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return lhs.pkgName.compareTo(rhs.pkgName);
        }
    }

    public static class UidComparator implements Comparator<AppInfo> {
        private PkgNameComparator mPkgNameComparator = new PkgNameComparator();

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if (lhs.appUid < rhs.appUid) {
                return -1;
            } else if (lhs.appUid > rhs.appUid) {
                return 1;
            } else {
                return mPkgNameComparator.compare(lhs, rhs);
            }
        }
    }

    public static class InstallTimeComparator implements Comparator<AppInfo> {
        private PkgNameComparator mPkgNameComparator = new PkgNameComparator();

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if (lhs.installTime < rhs.installTime) {
                return 1;
            } else if (lhs.installTime > rhs.installTime) {
                return -1;
            } else {
                return mPkgNameComparator.compare(lhs, rhs);
            }
        }
    }

    public static class UpdateTimeComparator implements Comparator<AppInfo> {
        private PkgNameComparator mPkgNameComparator = new PkgNameComparator();

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if (lhs.updateTime < rhs.updateTime) {
                return 1;
            } else if (lhs.updateTime > rhs.updateTime) {
                return -1;
            } else {
                return mPkgNameComparator.compare(lhs, rhs);
            }
        }
    }

}
