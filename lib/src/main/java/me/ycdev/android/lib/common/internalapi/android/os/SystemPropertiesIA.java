package me.ycdev.android.lib.common.internalapi.android.os;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.utils.LibLogger;

public class SystemPropertiesIA {
    private static final String TAG = "SystemBuildPropCompat";

    private static Method sMtd_get;
    private static Method sMtd_getInt;
    private static Method sMtd_getLong;
    private static Method sMtd_getBoolean;

    static {
        try {
            Class<?> classObj = Class.forName("android.os.SystemProperties", false,
                    Thread.currentThread().getContextClassLoader());
            sMtd_get = classObj.getMethod("get", String.class, String.class);
            sMtd_getInt = classObj.getMethod("getInt", String.class, int.class);
            sMtd_getLong = classObj.getMethod("getLong", String.class, long.class);
            sMtd_getBoolean = classObj.getMethod("getBoolean", String.class, boolean.class);
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to reflect SystemProperties", e);
        }
    }

    private SystemPropertiesIA() {
        // nothing to do
    }

    public static String get(String key, String def) {
        if (sMtd_get != null) {
            try {
                Object result = sMtd_get.invoke(null, key, def);
                return (String) result;
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, String)", e);
            }
        } else {
            LibLogger.w(TAG, "#get(String, String) not found");
        }
        return def;
    }

    public static int getInt(String key, int def) {
        if (sMtd_getInt != null) {
            try {
                Object result = sMtd_getInt.invoke(null, key, def);
                return (Integer) result;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, int)", e);
            }
        } else {
            LibLogger.w(TAG, "#getInt(String, int) not found");
        }
        return def;
    }

    public static long getLong(String key, long def) {
        if (sMtd_getLong != null) {
            try {
                Object result = sMtd_getLong.invoke(null, key, def);
                return (Long) result;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, long)", e);
            }
        } else {
            LibLogger.w(TAG, "#getLong(String, long) not found");
        }
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        if (sMtd_getBoolean != null) {
            try {
                Object result = sMtd_getBoolean.invoke(null, key, def);
                return (Boolean) result;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, boolean)", e);
            }
        } else {
            LibLogger.w(TAG, "#getBoolean(String, boolean) not found");
        }
        return def;
    }
}
