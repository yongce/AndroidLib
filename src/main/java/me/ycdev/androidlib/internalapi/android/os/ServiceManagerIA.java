package me.ycdev.androidlib.internalapi.android.os;

import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.androidlib.LibConfigs;
import me.ycdev.androidlib.utils.LibLogger;

public class ServiceManagerIA {
    private static final String TAG = "ServiceManagerIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static Method sMtd_getService;

    static {
        try {
            Class<?> smClass = Class.forName("android.os.ServiceManager", false,
                    Thread.currentThread().getContextClassLoader());
            sMtd_getService = smClass.getMethod("getService", String.class);
        } catch (ClassNotFoundException e) {
            if (DEBUG) LibLogger.w(TAG, "class not found", e);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    private ServiceManagerIA() {
        // nothing to do
    }

    public static IBinder getService(Object name) {
        if (sMtd_getService != null) {
            try {
                return (IBinder) sMtd_getService.invoke(null, name);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #getService()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #getService()", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#getService() not available");
        }
        return null;
    }
}
