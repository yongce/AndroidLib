package me.ycdev.androidlib.internalapi.android.os;

import android.content.Context;
import android.os.Build;
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

    /**
     * Go to sleep reason code: Going to sleep due by user request.
     */
    private static final int GO_TO_SLEEP_REASON_USER = 0;

    private static Method sMtdAsInterface;

    private static Class<?> sPowerMgrClass;
    private static Method sMtdReboot;
    private static int sMtdRebootVersion;
    private static Method sMtdShutdown;
    private static Method sMtdCrash;
    private static Method sMtdGoToSleep;

    static {
        try {
            Class<?> stubClass = Class.forName("android.os.IPowerManager$Stub", false,
                    Thread.currentThread().getContextClassLoader());
            sMtdAsInterface = stubClass.getMethod("asInterface", IBinder.class);

            sPowerMgrClass = Class.forName("android.os.IPowerManager", false,
                    Thread.currentThread().getContextClassLoader());
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

    public static Object getIPowerManager() {
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        if (binder != null) {
            return asInterface(binder);
        }
        return null;
    }

    private static void reflectMethodReboot() {
        if (sMtdReboot != null || sPowerMgrClass == null) {
            return;
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void reboot(String reason);
                sMtdReboot = sPowerMgrClass.getMethod("reboot", String.class);
                sMtdRebootVersion = API_VERSION_1;
            } catch (NoSuchMethodException e) {
                // Android 4.2: void reboot(boolean confirm, String reason, boolean wait);
                sMtdReboot = sPowerMgrClass.getMethod("reboot",
                        boolean.class, String.class, boolean.class);
                sMtdRebootVersion = API_VERSION_2;
            }
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Reboot the device.
     * @param service The "android.os.IPowerManager" object.
     * @param reason
     * @see #asInterface(android.os.IBinder)
     */
    public static void reboot(Object service, String reason) {
        reflectMethodReboot();
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

    /**
     * Just for unit test.
     */
    static boolean checkRebootReflect() {
        reflectMethodReboot();
        return sMtdReboot != null;
    }

    private static void reflectMethodShutdown() {
        if (sMtdShutdown != null || sPowerMgrClass == null) {
            return;
        }

        try {
            // Android 4.2: void shutdown(boolean confirm, boolean wait);
            sMtdShutdown = sPowerMgrClass.getMethod("shutdown", boolean.class, boolean.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void shutdown(Object service) {
        reflectMethodShutdown();
        if (sMtdShutdown != null) {
            try {
                sMtdShutdown.invoke(service, false, false);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #shutdown()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #shutdown()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#shutdown() not available");
        }
    }

    /**
     * Just for unit test.
     */
    static boolean checkShutdownReflect() {
        reflectMethodShutdown();
        return sMtdShutdown != null;
    }

    private static void reflectMethodCrash() {
        if (sMtdCrash != null || sPowerMgrClass == null) {
            return;
        }

        try {
            // Android 2.2 and next versions: void crash(String message);
            sMtdCrash = sPowerMgrClass.getMethod("crash", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void crash(Object service, String msg) {
        reflectMethodCrash();
        if (sMtdCrash != null) {
            try {
                sMtdCrash.invoke(service, msg);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#crash() not available");
        }
    }

    /**
     * Just for unit test.
     */
    static boolean checkCrashReflect() {
        reflectMethodCrash();
        return sMtdCrash != null;
    }

    private static void reflectGoToSleep() {
        if (sMtdGoToSleep != null || sPowerMgrClass == null) {
            return;
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void goToSleepWithReason(long time, int reason);
                sMtdGoToSleep = sPowerMgrClass.getMethod("goToSleepWithReason", long.class, int.class);
            } catch (NoSuchMethodException e) {
                // Android 4.2: void goToSleep(long time, int reason);
                sMtdGoToSleep = sPowerMgrClass.getMethod("goToSleep", long.class, int.class);
            }
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Forces the device to go to sleep.
     * @param service
     * @param time The time when the request to go to sleep was issued,
     *             in the {@link android.os.SystemClock#uptimeMillis()} time base.
     *             This timestamp is used to correctly order the go to sleep request with
     *             other power management functions. It should be set to the timestamp
     *             of the input event that caused the request to go to sleep.
     * @see android.os.PowerManager#goToSleep(long)
     */
    public static void goToSleep(Object service, long time) {
        reflectGoToSleep();
        if (sMtdGoToSleep != null) {
            try {
                sMtdGoToSleep.invoke(service, time, GO_TO_SLEEP_REASON_USER);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#crash() not available");
        }
    }

    static boolean checkGoToSleepReflect() {
        reflectGoToSleep();
        return sMtdGoToSleep != null;
    }
}