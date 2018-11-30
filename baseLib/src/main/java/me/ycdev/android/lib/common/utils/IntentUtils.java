package me.ycdev.android.lib.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class IntentUtils {
    public static boolean canStartActivity(@NonNull Context cxt, @NonNull Intent activityIntent) {
        // Use PackageManager.MATCH_DEFAULT_ONLY to behavior same as Context#startAcitivty()
        ResolveInfo resolveInfo = cxt.getPackageManager().resolveActivity(activityIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null;
    }
}
