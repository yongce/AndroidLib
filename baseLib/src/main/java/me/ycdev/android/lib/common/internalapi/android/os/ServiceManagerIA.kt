package me.ycdev.android.lib.common.internalapi.android.os

import android.annotation.SuppressLint
import android.os.IBinder
import androidx.annotation.RestrictTo
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import timber.log.Timber

@Suppress("unused")
@SuppressLint("PrivateApi")
object ServiceManagerIA {
    private const val TAG = "ServiceManagerIA"

    private var sClass_ServiceManager: Class<*>? = null

    private var sMtd_getService: Method? = null
    private var sMtd_checkService: Method? = null
    private var sMtd_addService: Method? = null
    private var sMtd_listServices: Method? = null

    init {
        try {
            sClass_ServiceManager = Class.forName(
                "android.os.ServiceManager", false,
                Thread.currentThread().contextClassLoader
            )
        } catch (e: ClassNotFoundException) {
            Timber.tag(TAG).w(e, "class not found")
        }
    }

    private fun reflectGetService() {
        if (sMtd_getService != null || sClass_ServiceManager == null) {
            return
        }

        try {
            // public static IBinder getService(String name)
            sMtd_getService = sClass_ServiceManager!!.getMethod("getService", String::class.java)
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Returns a reference to a service with the given name.
     *
     *
     * Important: May block the calling thread!
     * @param name the name of the service to get
     * @return a reference to the service, or `null` if the service doesn't exist
     */
    fun getService(name: String): IBinder? {
        reflectGetService()
        if (sMtd_getService != null) {
            try {
                return sMtd_getService!!.invoke(null, name) as IBinder
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getService()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getService() ag")
            }
        } else {
            Timber.tag(TAG).w("#getService() not available")
        }
        return null
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectGetService(): Boolean {
        reflectGetService()
        return sMtd_getService != null
    }

    private fun reflectCheckService() {
        if (sMtd_checkService != null || sClass_ServiceManager == null) {
            return
        }

        try {
            // public static IBinder checkService(String name)
            sMtd_checkService =
                sClass_ServiceManager!!.getMethod("checkService", String::class.java)
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Retrieve an existing service called @a name from the
     * service manager.  Non-blocking.
     */
    fun checkService(name: String): IBinder? {
        reflectCheckService()
        if (sMtd_checkService != null) {
            try {
                return sMtd_checkService!!.invoke(null, name) as IBinder
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #checkService()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #checkService() ag")
            }
        } else {
            Timber.tag(TAG).w("#checkService() not available")
        }
        return null
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectCheckService(): Boolean {
        reflectCheckService()
        return sMtd_checkService != null
    }

    private fun reflectAddService() {
        if (sMtd_addService != null || sClass_ServiceManager == null) {
            return
        }

        try {
            // public static void addService(String name, IBinder service)
            sMtd_addService = sClass_ServiceManager!!.getMethod(
                "addService",
                String::class.java, IBinder::class.java
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Place a new @a service called @a name into the service
     * manager.
     *
     * @param name the name of the new service
     * @param service the service object
     */
    fun addService(name: String, service: IBinder) {
        reflectAddService()
        if (sMtd_addService != null) {
            try {
                sMtd_addService!!.invoke(null, name, service)
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #addService()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #addService() ag")
            }
        } else {
            Timber.tag(TAG).w("#addService() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectAddService(): Boolean {
        reflectAddService()
        return sMtd_addService != null
    }

    private fun reflectListServices() {
        if (sMtd_listServices != null || sClass_ServiceManager == null) {
            return
        }

        try {
            // public static String[] listServices() throws RemoteException
            sMtd_listServices = sClass_ServiceManager!!.getMethod("listServices")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Return a list of all currently running services.
     */
    fun listServices(): Array<String>? {
        reflectListServices()
        if (sMtd_listServices != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                return sMtd_listServices!!.invoke(null) as Array<String>
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #listServices()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #listServices() more")
            }
        } else {
            Timber.tag(TAG).w("#listServices() not available")
        }
        return null
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectListServices(): Boolean {
        reflectListServices()
        return sMtd_listServices != null
    }
}
