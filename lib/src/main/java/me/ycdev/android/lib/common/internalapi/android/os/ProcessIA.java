package me.ycdev.android.lib.common.internalapi.android.os;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

public class ProcessIA {
    private static final String TAG = "ProcessIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static Method sMtd_setArgV0;

    private static void reflect_setArgV0() {
        if (sMtd_setArgV0 != null) {
            return;
        }

        try {
            sMtd_setArgV0 = android.os.Process.class.getMethod("setArgV0", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void setArgV0(String processName) {
        reflect_setArgV0();
        if (sMtd_setArgV0 != null) {
            try {
                sMtd_setArgV0.invoke(null, processName);
            } catch (IllegalAccessException | InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #setArgV0()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#reboot() not available");
        }
    }

    /**
     * Just for unit test.
     */
    static boolean checkReflect_setArgV0() {
        reflect_setArgV0();
        return sMtd_setArgV0 != null;
    }
}
