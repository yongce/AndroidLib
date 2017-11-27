package me.ycdev.android.lib.common.internalapi.android.os;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

@SuppressWarnings({"unused", "WeakerAccess"})
@SuppressLint("PrivateApi")
public class ServiceManagerIA {
    private static final String TAG = "ServiceManagerIA";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static Class<?> sClass_ServiceManager;

    private static Method sMtd_getService;
    private static Method sMtd_checkService;
    private static Method sMtd_addService;
    private static Method sMtd_listServices;

    static {
        try {
            sClass_ServiceManager = Class.forName("android.os.ServiceManager", false,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            if (DEBUG) LibLogger.w(TAG, "class not found", e);
        }
    }

    private ServiceManagerIA() {
        // nothing to do
    }

    private static void reflect_getService() {
        if (sMtd_getService != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static IBinder getService(String name)
            sMtd_getService = sClass_ServiceManager.getMethod("getService", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Returns a reference to a service with the given name.
     *
     * <p>Important: May block the calling thread!</p>
     * @param name the name of the service to get
     * @return a reference to the service, or <code>null</code> if the service doesn't exist
     */
    @Nullable
    public static IBinder getService(@NonNull String name) {
        reflect_getService();
        if (sMtd_getService != null) {
            try {
                return (IBinder) sMtd_getService.invoke(null, name);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #getService()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #getService() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#getService() not available");
        }
        return null;
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_getService() {
        reflect_getService();
        return sMtd_getService != null;
    }

    private static void reflect_checkService() {
        if (sMtd_checkService != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static IBinder checkService(String name)
            sMtd_checkService = sClass_ServiceManager.getMethod("checkService", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Retrieve an existing service called @a name from the
     * service manager.  Non-blocking.
     */
    @Nullable
    public static IBinder checkService(@NonNull String name) {
        reflect_checkService();
        if (sMtd_checkService != null) {
            try {
                return (IBinder) sMtd_checkService.invoke(null, name);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #checkService()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #checkService() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#checkService() not available");
        }
        return null;
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_checkService() {
        reflect_checkService();
        return sMtd_checkService != null;
    }

    private static void reflect_addService() {
        if (sMtd_addService != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static void addService(String name, IBinder service)
            sMtd_addService = sClass_ServiceManager.getMethod("addService",
                    String.class, IBinder.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Place a new @a service called @a name into the service
     * manager.
     *
     * @param name the name of the new service
     * @param service the service object
     */
    public static void addService(@NonNull String name, @NonNull IBinder service) {
        reflect_addService();
        if (sMtd_addService != null) {
            try {
                sMtd_addService.invoke(null, name, service);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #addService()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #addService() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#addService() not available");
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_addService() {
        reflect_addService();
        return sMtd_addService != null;
    }

    private static void reflect_listServices() {
        if (sMtd_listServices != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static String[] listServices() throws RemoteException
            sMtd_listServices = sClass_ServiceManager.getMethod("listServices");
        } catch (NoSuchMethodException e) {
            if (DEBUG) LibLogger.w(TAG, "method not found", e);
        }
    }

    /**
     * Return a list of all currently running services.
     */
    @Nullable
    public static String[] listServices() {
        reflect_listServices();
        if (sMtd_listServices != null) {
            try {
                return (String[]) sMtd_listServices.invoke(null);
            } catch (IllegalAccessException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #listServices()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) LibLogger.w(TAG, "Failed to invoke #listServices() more", e);
            }
        } else {
            if (DEBUG) LibLogger.w(TAG, "#listServices() not available");
        }
        return null;
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    static boolean checkReflect_listServices() {
        reflect_listServices();
        return sMtd_listServices != null;
    }
}
