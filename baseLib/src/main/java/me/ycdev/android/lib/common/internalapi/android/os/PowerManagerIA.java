package me.ycdev.android.lib.common.internalapi.android.os;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

@SuppressWarnings({"unused", "WeakerAccess"})
@SuppressLint("PrivateApi")
public class PowerManagerIA {
    private static final String TAG = "PowerManagerIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static final int API_VERSION_1 = 1;
    private static final int API_VERSION_2 = 2;

    /**
     * Go to sleep reason code: Going to sleep due by user request.
     */
    private static final int GO_TO_SLEEP_REASON_USER = 0;

    private static Method sMtd_asInterface;

    private static Class<?> sClass_IPowerManager;
    private static Method sMtd_reboot;
    private static int sVersion_reboot;
    private static Method sMtd_shutdown;
    private static int sVersion_shutdown;
    private static Method sMtd_crash;
    private static Method sMtd_goToSleep;
    private static int sVersion_goToSleep;

    static {
        try {
            Class<?> stubClass = Class.forName("android.os.IPowerManager$Stub", false,
                    Thread.currentThread().getContextClassLoader());
            sMtd_asInterface = stubClass.getMethod("asInterface", IBinder.class);

            sClass_IPowerManager = Class.forName("android.os.IPowerManager", false,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            if (DEBUG) LibLogger.w(TAG, "class not found", e);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    private PowerManagerIA() {
        // nothing to do
    }

    /**
     * Get "android.os.IPowerManager" object from the service binder.
     * @return null will be returned if failed
     */
    @Nullable
    public static Object asInterface(@NonNull IBinder binder) {
        if (sMtd_asInterface != null) {
            try {
                return sMtd_asInterface.invoke(null, binder);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #asInterface()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #asInterface() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#asInterface() not available");
        }
        return null;
    }

    /**
     * Get "android.os.IPowerManager" object from the service manager.
     * @return null will be returned if failed
     */
    @Nullable
    public static Object getIPowerManager() {
        IBinder binder = ServiceManagerIA.getService(Context.POWER_SERVICE);
        if (binder != null) {
            return asInterface(binder);
        }
        return null;
    }

    private static void reflect_reboot() {
        if (sMtd_reboot != null || sClass_IPowerManager == null) {
            return;
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void reboot(String reason);
                sMtd_reboot = sClass_IPowerManager.getMethod("reboot", String.class);
                sVersion_reboot = API_VERSION_1;
            } catch (NoSuchMethodException e) {
                // Android 4.2: void reboot(boolean confirm, String reason, boolean wait);
                sMtd_reboot = sClass_IPowerManager.getMethod("reboot",
                        boolean.class, String.class, boolean.class);
                sVersion_reboot = API_VERSION_2;
            }
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Reboot the device.
     * @param service The "android.os.IPowerManager" object.
     * @param reason Just for logging
     * @see #asInterface(android.os.IBinder)
     */
    public static void reboot(@NonNull Object service, @NonNull String reason) {
        reflect_reboot();
        if (sMtd_reboot != null) {
            try {
                if (sVersion_reboot == API_VERSION_1) {
                    sMtd_reboot.invoke(service, reason);
                } else if (sVersion_reboot == API_VERSION_2) {
                    sMtd_reboot.invoke(service, false, reason, false);
                } else {
                    if (DEBUG) LibLogger.e(TAG, "reboot, unknown api version: " + sVersion_reboot);
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #reboot()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #reboot() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#reboot() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_reboot() {
        reflect_reboot();
        return sMtd_reboot != null;
    }

    private static void reflect_shutdown() {
        if (sMtd_shutdown != null || sClass_IPowerManager == null ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }

        try {
            try {
                // Android 4.2: void shutdown(boolean confirm, boolean wait);
                sMtd_shutdown = sClass_IPowerManager.getMethod("shutdown",
                        boolean.class, boolean.class);
                sVersion_shutdown = API_VERSION_1;
            } catch (NoSuchMethodException e) {
                // Android 7.0: void shutdown(boolean confirm, String reason, boolean wait);
                sMtd_shutdown = sClass_IPowerManager.getMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                sVersion_shutdown = API_VERSION_2;
            }
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void shutdown(@NonNull Object service, String reason) {
        reflect_shutdown();
        if (sMtd_shutdown != null) {
            try {
                if (sVersion_shutdown == API_VERSION_1) {
                    sMtd_shutdown.invoke(service, false, false);
                } else if (sVersion_shutdown == API_VERSION_2) {
                    sMtd_shutdown.invoke(service, false, reason, false);
                } else {
                    if (DEBUG) LibLogger.e(TAG, "shutdown, unknown api version: " + sVersion_shutdown);
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #shutdown()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #shutdown() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#shutdown() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_shutdown() {
        reflect_shutdown();
        return sMtd_shutdown != null || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    private static void reflect_crash() {
        if (sMtd_crash != null || sClass_IPowerManager == null) {
            return;
        }

        try {
            // Android 2.2 and next versions: void crash(String message);
            sMtd_crash = sClass_IPowerManager.getMethod("crash", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    public static void crash(@NonNull Object service, @NonNull String msg) {
        reflect_crash();
        if (sMtd_crash != null) {
            try {
                sMtd_crash.invoke(service, msg);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#crash() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_crash() {
        reflect_crash();
        return sMtd_crash != null;
    }

    private static void reflect_goToSleep() {
        if (sMtd_goToSleep != null || sClass_IPowerManager == null) {
            return;
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void goToSleepWithReason(long time, int reason);
                sMtd_goToSleep = sClass_IPowerManager.getMethod("goToSleepWithReason", long.class, int.class);
                sVersion_goToSleep = API_VERSION_1;
            } catch (NoSuchMethodException e) {
                try {
                    // Android 4.2: void goToSleep(long time, int reason);
                    sMtd_goToSleep = sClass_IPowerManager.getMethod("goToSleep", long.class, int.class);
                    sVersion_goToSleep = API_VERSION_1;
                } catch (NoSuchMethodException e1) {
                    // Android 5.0: void goToSleep(long time, int reason, int flags);
                    sMtd_goToSleep = sClass_IPowerManager.getMethod("goToSleep", long.class, int.class, int.class);
                    sVersion_goToSleep = API_VERSION_2;
                }

            }
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Forces the device to go to sleep. Please refer android.os.PowerManager#goToSleep(long).
     * @param service The IPowerManager object
     * @param time The time when the request to go to sleep was issued,
     *             in the {@link android.os.SystemClock#uptimeMillis()} time base.
     *             This timestamp is used to correctly order the go to sleep request with
     *             other power management functions. It should be set to the timestamp
     *             of the input event that caused the request to go to sleep.
     */
    public static void goToSleep(@NonNull Object service, long time) {
        reflect_goToSleep();
        if (sMtd_goToSleep != null) {
            try {
                if (sVersion_goToSleep == API_VERSION_1) {
                    sMtd_goToSleep.invoke(service, time, GO_TO_SLEEP_REASON_USER);
                } else if (sVersion_goToSleep == API_VERSION_2) {
                    sMtd_goToSleep.invoke(service, time, GO_TO_SLEEP_REASON_USER, 0);
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #crash() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#crash() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_goToSleep() {
        reflect_goToSleep();
        return sMtd_goToSleep != null;
    }
}