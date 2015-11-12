package me.ycdev.android.lib.common.compat;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.PowerManager;

public class PowerManagerCompat {
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static boolean isScreenOn(PowerManager pm) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return pm.isInteractive();
        } else {
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }
}
