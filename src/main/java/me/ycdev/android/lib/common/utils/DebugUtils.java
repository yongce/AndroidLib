package me.ycdev.android.lib.common.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

public class DebugUtils {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (AndroidVersionUtils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (AndroidVersionUtils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
            }

            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }
}
