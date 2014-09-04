package me.ycdev.androidlib.internalapi.android.os;

import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.androidlib.LibConfigs;
import me.ycdev.androidlib.utils.LibLogger;

public class PowerManagerIA {
    private static final String TAG = "PowerManagerIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static final int API_VERSION_1 = 1;
    private static final int API_VERSION_2 = 2;

    private static Method sMtdAsInterface;
    private static Method sMtdReboot;
    private static int sMtdRebootVersion;

    static {
        try {
            Class<?> stubClass = Class.forName("android.os.IPowerManager$Stub", false,
                    Thread.currentThread().getContextClassLoader());
            sMtdAsInterface = stubClass.getMethod("asInterface", IBinder.class);

            Class<?>  powerMgrClass = Class.forName("android.os.IPowerManager", false,
                    Thread.currentThread().getContextClassLoader());
            try {
                // Android 4.1 and older versions: void reboot(String reason);
                sMtdReboot = powerMgrClass.getMethod("reboot", String.class);
                sMtdRebootVersion = API_VERSION_1;
            } catch (NoSuchMethodException e) {
                // Android 4.2: void reboot(boolean confirm, String reason, boolean wait);
                sMtdReboot = powerMgrClass.getMethod("reboot",
                        boolean.class, String.class, boolean.class);
                sMtdRebootVersion = API_VERSION_2;
            }
        } catch (ClassNotFoundException e) {
            if (DEBUG) LibLogger.w(TAG, "class not found", e);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Get "android.os.IPowerManager" object from the service binder.
     * @return null will be returned if failed
     */
    public static Object asInterface(IBinder binder) {
        if (sMtdAsInterface != null) {
            try {
                return sMtdAsInterface.invoke(null, binder);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #asInterface()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #asInterface()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#asInterface() not available");
        }
        return null;
    }

    /**
     * Reboot the device.
     * @param service The "android.os.IPowerManager" object.
     * @param reason
     * @see #asInterface(android.os.IBinder)
     */
    public static void reboot(Object service, String reason) {
        if (sMtdReboot != null) {
            try {
                if (sMtdRebootVersion == API_VERSION_1) {
                    sMtdReboot.invoke(service, reason);
                } else if (sMtdRebootVersion == API_VERSION_2) {
                    sMtdReboot.invoke(service, false, reason, false);
                } else {
                    if (DEBUG) LibLogger.e(TAG, "reboot, unknown api version: " + sMtdRebootVersion);
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #reboot()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #reboot()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#reboot() not available");
        }
    }

}