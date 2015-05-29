package me.ycdev.android.lib.common.internalapi.android.app;

import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.annotation.VisibleForTesting;
import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIA;
import me.ycdev.android.lib.common.internalapi.android.os.UserHandleIA;
import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

public class ActivityManagerIA {
    private static final String TAG = "ActivityManagerIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static final int API_VERSION_1 = 1;
    private static final int API_VERSION_2 = 2;

    private static Method sMtd_asInterface;

    private static Class<?> sClass_IActivityManager;
    private static Method sMtd_forceStopPackage;
    private static int sVersion_forceStopPackage;

    static {
        try {
            Class<?> stubClass = Class.forName("android.app.ActivityManagerNative", false,
                    Thread.currentThread().getContextClassLoader());
            sMtd_asInterface = stubClass.getMethod("asInterface", IBinder.class);

            sClass_IActivityManager = Class.forName("android.app.IActivityManager", false,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            if (DEBUG) LibLogger.w(TAG, "class not found", e);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    private ActivityManagerIA() {
        // nothing to do
    }

    /**
     * Get "android.os.IActivityManager" object from the service binder.
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
     * Get "android.os.IActivityManager" object from the service manager.
     * @return null will be returned if failed
     */
    @Nullable
    public static Object getIActivityManager() {
        IBinder binder = ServiceManagerIA.getService(Context.ACTIVITY_SERVICE);
        if (binder != null) {
            return asInterface(binder);
        }
        return null;
    }

    private static void reflect_forceStopPackage() {
        if (sMtd_forceStopPackage != null || sClass_IActivityManager == null) {
            return;
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void forceStopPackage(String packageName);
                sMtd_forceStopPackage = sClass_IActivityManager.getMethod("forceStopPackage", String.class);
                sVersion_forceStopPackage = API_VERSION_1;
            } catch (NoSuchMethodException e) {
                // Android 4.2: void forceStopPackage(String packageName, int userId);
                sMtd_forceStopPackage = sClass_IActivityManager.getMethod("forceStopPackage",
                        String.class, int.class);
                sVersion_forceStopPackage = API_VERSION_2;
            }
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Force stop the specified app.
     * @param service The "android.os.IActivityManager" object.
     * @param pkgName The package name of the app
     * @see #asInterface(android.os.IBinder)
     */
    public static void forceStopPackage(@NonNull Object service, @NonNull String pkgName) {
        reflect_forceStopPackage();
        if (sMtd_forceStopPackage != null) {
            try {
                if (sVersion_forceStopPackage == API_VERSION_1) {
                    sMtd_forceStopPackage.invoke(service, pkgName);
                } else if (sVersion_forceStopPackage == API_VERSION_2) {
                    sMtd_forceStopPackage.invoke(service, pkgName, UserHandleIA.myUserId());
                } else {
                    if (DEBUG) LibLogger.e(TAG, "reboot, unknown api version: " + sVersion_forceStopPackage);
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #forceStopPackage()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #forceStopPackage() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#forceStopPackage() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @VisibleForTesting
    static boolean checkReflect_forceStopPackage() {
        reflect_forceStopPackage();
        return sMtd_forceStopPackage != null;
    }

}
