package me.ycdev.android.lib.common.internalapi.android.os

import android.annotation.SuppressLint
import android.content.Context
import android.os.IBinder
import androidx.annotation.RestrictTo
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Suppress("unused")
@SuppressLint("PrivateApi")
object PowerManagerIA {
    private const val TAG = "PowerManagerIA"

    private const val API_VERSION_1 = 1
    private const val API_VERSION_2 = 2

    /**
     * Go to sleep reason code: Going to sleep due by user request.
     */
    private const val GO_TO_SLEEP_REASON_USER = 0

    private var sMtd_asInterface: Method? = null

    private var sClass_IPowerManager: Class<*>? = null
    private var sMtd_reboot: Method? = null
    private var sVersion_reboot: Int = 0
    private var sMtd_shutdown: Method? = null
    private var sVersion_shutdown: Int = 0
    private var sMtd_crash: Method? = null
    private var sMtd_goToSleep: Method? = null
    private var sVersion_goToSleep: Int = 0

    /**
     * Get "android.os.IPowerManager" object from the service manager.
     * @return null will be returned if failed
     */
    val iPowerManager: Any?
        get() {
            val binder = ServiceManagerIA.getService(Context.POWER_SERVICE)
            return if (binder != null) {
                asInterface(binder)
            } else {
                null
            }
        }

    init {
        try {
            val stubClass = Class.forName(
                "android.os.IPowerManager\$Stub", false,
                Thread.currentThread().contextClassLoader
            )
            sMtd_asInterface = stubClass.getMethod("asInterface", IBinder::class.java)

            sClass_IPowerManager = Class.forName(
                "android.os.IPowerManager", false,
                Thread.currentThread().contextClassLoader
            )
        } catch (e: ClassNotFoundException) {
            Timber.tag(TAG).w(e, "class not found")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Get "android.os.IPowerManager" object from the service binder.
     * @return null will be returned if failed
     */
    fun asInterface(binder: IBinder): Any? {
        if (sMtd_asInterface != null) {
            try {
                return sMtd_asInterface!!.invoke(null, binder)
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

    private fun reflectReboot() {
        if (sMtd_reboot != null || sClass_IPowerManager == null) {
            return
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void reboot(String reason);
                sMtd_reboot = sClass_IPowerManager!!.getMethod("reboot", String::class.java)
                sVersion_reboot = API_VERSION_1
            } catch (e: NoSuchMethodException) {
                // Android 4.2: void reboot(boolean confirm, String reason, boolean wait);
                sMtd_reboot = sClass_IPowerManager!!.getMethod(
                    "reboot",
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType
                )
                sVersion_reboot = API_VERSION_2
            }
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Reboot the device.
     * @param service The "android.os.IPowerManager" object.
     * @param reason Just for logging
     * @see .asInterface
     */
    fun reboot(service: Any, reason: String) {
        reflectReboot()
        if (sMtd_reboot != null) {
            try {
                when (sVersion_reboot) {
                    API_VERSION_1 -> sMtd_reboot!!.invoke(service, reason)
                    API_VERSION_2 -> sMtd_reboot!!.invoke(service, false, reason, false)
                    else -> Timber.tag(TAG).e("reboot, unknown api version: $sVersion_reboot")
                }
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #reboot()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #reboot() more")
            }
        } else {
            Timber.tag(TAG).w("#reboot() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectReboot(): Boolean {
        reflectReboot()
        return sMtd_reboot != null
    }

    private fun reflectShutdown() {
        if (sMtd_shutdown != null || sClass_IPowerManager == null) return

        try {
            try {
                // Android 4.2: void shutdown(boolean confirm, boolean wait);
                sMtd_shutdown = sClass_IPowerManager!!.getMethod(
                    "shutdown",
                    Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
                )
                sVersion_shutdown = API_VERSION_1
            } catch (e: NoSuchMethodException) {
                // Android 7.0: void shutdown(boolean confirm, String reason, boolean wait);
                sMtd_shutdown = sClass_IPowerManager!!.getMethod(
                    "shutdown",
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType
                )
                sVersion_shutdown = API_VERSION_2
            }
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun shutdown(service: Any, reason: String) {
        reflectShutdown()
        if (sMtd_shutdown != null) {
            try {
                when (sVersion_shutdown) {
                    API_VERSION_1 -> sMtd_shutdown!!.invoke(service, false, false)
                    API_VERSION_2 -> sMtd_shutdown!!.invoke(service, false, reason, false)
                    else -> Timber.tag(TAG).e("shutdown, unknown api version: $sVersion_shutdown")
                }
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #shutdown()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #shutdown() more")
            }
        } else {
            Timber.tag(TAG).w("#shutdown() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectShutdown(): Boolean {
        reflectShutdown()
        return sMtd_shutdown != null
    }

    private fun reflectCrash() {
        if (sMtd_crash != null || sClass_IPowerManager == null) {
            return
        }

        try {
            // Android 2.2 and next versions: void crash(String message);
            sMtd_crash = sClass_IPowerManager!!.getMethod("crash", String::class.java)
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    fun crash(service: Any, msg: String) {
        reflectCrash()
        if (sMtd_crash != null) {
            try {
                sMtd_crash!!.invoke(service, msg)
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #crash()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #crash() more")
            }
        } else {
            Timber.tag(TAG).w("#crash() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectCrash(): Boolean {
        reflectCrash()
        return sMtd_crash != null
    }

    private fun reflectGoToSleep() {
        if (sMtd_goToSleep != null || sClass_IPowerManager == null) {
            return
        }

        try {
            try {
                // Android 2.2 ~ Android 4.1: void goToSleepWithReason(long time, int reason);
                sMtd_goToSleep = sClass_IPowerManager!!.getMethod(
                    "goToSleepWithReason",
                    Long::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                sVersion_goToSleep = API_VERSION_1
            } catch (e: NoSuchMethodException) {
                try {
                    // Android 4.2: void goToSleep(long time, int reason);
                    sMtd_goToSleep = sClass_IPowerManager!!.getMethod(
                        "goToSleep",
                        Long::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                    sVersion_goToSleep = API_VERSION_1
                } catch (e1: NoSuchMethodException) {
                    // Android 5.0: void goToSleep(long time, int reason, int flags);
                    sMtd_goToSleep = sClass_IPowerManager!!.getMethod(
                        "goToSleep",
                        Long::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                    sVersion_goToSleep = API_VERSION_2
                }
            }
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "method not found")
        }
    }

    /**
     * Forces the device to go to sleep. Please refer android.os.PowerManager#goToSleep(long).
     * @param service The IPowerManager object
     * @param time The time when the request to go to sleep was issued,
     * in the [android.os.SystemClock.uptimeMillis] time base.
     * This timestamp is used to correctly order the go to sleep request with
     * other power management functions. It should be set to the timestamp
     * of the input event that caused the request to go to sleep.
     */
    fun goToSleep(service: Any, time: Long) {
        reflectGoToSleep()
        if (sMtd_goToSleep != null) {
            try {
                if (sVersion_goToSleep == API_VERSION_1) {
                    sMtd_goToSleep!!.invoke(service, time, GO_TO_SLEEP_REASON_USER)
                } else if (sVersion_goToSleep == API_VERSION_2) {
                    sMtd_goToSleep!!.invoke(service, time, GO_TO_SLEEP_REASON_USER, 0)
                }
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #crash()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #crash() more")
            }
        } else {
            Timber.tag(TAG).w("#crash() not available")
        }
    }

    /**
     * Just for unit test.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun checkReflectGoToSleep(): Boolean {
        reflectGoToSleep()
        return sMtd_goToSleep != null
    }
}
