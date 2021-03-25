package me.ycdev.android.lib.common.internalapi.android.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.IBinder
import androidx.annotation.RestrictTo
import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIA
import me.ycdev.android.lib.common.internalapi.android.os.UserHandleIA
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Suppress("unused")
@SuppressLint("PrivateApi")
object ActivityManagerIA {
    private const val TAG = "ActivityManagerIA"

    private const val API_VERSION_1 = 1
    private const val API_VERSION_2 = 2

    private var mtd_asInterface: Method? = null

    private var class_IActivityManager: Class<*>? = null
    private var mtd_forceStopPackage: Method? = null
    private var version_forceStopPackage: Int = 0

    init {
        try {
            val stubClass = Class.forName(
                "android.app.ActivityManagerNative", false,
                Thread.currentThread().contextClassLoader
            )
            mtd_asInterface = stubClass.getMethod("asInterface", IBinder::class.java)

            class_IActivityManager = Class.forName(
                "android.app.IActivityManager", false,
                Thread.currentThread().contextClassLoader
            )
        } catch (e: ClassNotFoundException) {
            Timber.tag(TAG).w(e, "class not found")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Get "android.os.IActivityManager" object from the service manager.
     * @return null will be returned if failed
     */
    fun getIActivityManager(): Any? {
        val binder = ServiceManagerIA.getService(Context.ACTIVITY_SERVICE) ?: return null
        return asInterface(binder)
    }

    /**
     * Get "android.os.IActivityManager" object from the service binder.
     * @return null will be returned if failed
     */
    fun asInterface(binder: IBinder): Any? {
        if (mtd_asInterface != null) {
            try {
                return mtd_asInterface!!.invoke(null, binder)
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #asInterface()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #asInterface() more")
            }
        } else {
            Timber.tag(TAG).w("#asInterface() not available")
        }
        return null
    }

    private fun reflectForceStopPackage() {
        if (mtd_forceStopPackage != null || class_IActivityManager == null) {
            return
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void forceStopPackage(String packageName);
                mtd_forceStopPackage =
                    class_IActivityManager!!.getMethod("forceStopPackage", String::class.java)
                version_forceStopPackage = API_VERSION_1
            } catch (e: NoSuchMethodException) {
                // Android 4.2: void forceStopPackage(String packageName, int userId);
                mtd_forceStopPackage = class_IActivityManager!!.getMethod(
                    "forceStopPackage",
                    String::class.java, Int::class.javaPrimitiveType
                )
                version_forceStopPackage = API_VERSION_2
            }
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Force stop the specified app.
     * @param service The "android.os.IActivityManager" object.
     * @param pkgName The package name of the app
     * @see .asInterface
     */
    fun forceStopPackage(service: Any, pkgName: String) {
        reflectForceStopPackage()
        if (mtd_forceStopPackage != null) {
            try {
                when (version_forceStopPackage) {
                    API_VERSION_1 -> mtd_forceStopPackage!!.invoke(service, pkgName)
                    API_VERSION_2 -> mtd_forceStopPackage!!.invoke(service, pkgName, UserHandleIA.myUserId())
                    else -> Timber.tag(TAG).e(
                        "reboot, unknown api version: $version_forceStopPackage"
                    )
                }
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #forceStopPackage()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #forceStopPackage() more")
            }
        } else {
            Timber.tag(TAG).w("#forceStopPackage() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectForceStopPackage(): Boolean {
        reflectForceStopPackage()
        return mtd_forceStopPackage != null
    }
}
