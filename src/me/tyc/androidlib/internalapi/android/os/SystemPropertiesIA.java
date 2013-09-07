package me.tyc.androidlib.internalapi.android.os;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.tyc.androidlib.internalapi.utils.LibLogger;

public class SystemPropertiesIA {
    private static final String TAG = "SystemBuildPropCompat";

    private static Method sMtdGet2;
    private static Method sMtdGetInt;
    private static Method sMtdGetLong;
    private static Method sMtdGetBoolean;

    static {
        try {
            Class<?> classObj = Class.forName("android.os.SystemProperties", false,
                    Thread.currentThread().getContextClassLoader());
            sMtdGet2 = classObj.getMethod("get", String.class, String.class);
            sMtdGetInt = classObj.getMethod("getInt", String.class, int.class);
            sMtdGetLong = classObj.getMethod("getLong", String.class, long.class);
            sMtdGetBoolean = classObj.getMethod("getBoolean", String.class, boolean.class);
        } catch (Exception e) {
            LibLogger.w(TAG, "Failed to reflect SystemProperties", e);
        }
    }

    public static String get(String key, String def) {
        if (sMtdGet2 != null) {
            try {
                Object result = sMtdGet2.invoke(null, key, def);
                return (String) result;
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, String)", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, String)", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, String)", e);
            }
        } else {
            LibLogger.w(TAG, "#get(String, String) not found");
        }
        return def;
    }

    public static int getInt(String key, int def) {
        if (sMtdGetInt != null) {
            try {
                Object result = sMtdGetInt.invoke(null, key, def);
                return (Integer) result;
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, int)", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, int)", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, int)", e);
            }
        } else {
            LibLogger.w(TAG, "#getInt(String, int) not found");
        }
        return def;
    }

    public static long getLong(String key, long def) {
        if (sMtdGetLong != null) {
            try {
                Object result = sMtdGetLong.invoke(null, key, def);
                return (Long) result;
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, long)", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, long)", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, long)", e);
            }
        } else {
            LibLogger.w(TAG, "#getLong(String, long) not found");
        }
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        if (sMtdGetBoolean != null) {
            try {
                Object result = sMtdGetBoolean.invoke(null, key, def);
                return (Boolean) result;
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, boolean)", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, boolean)", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke get(String, boolean)", e);
            }
        } else {
            LibLogger.w(TAG, "#getBoolean(String, boolean) not found");
        }
        return def;
    }
}
