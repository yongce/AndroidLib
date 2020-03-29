package me.ycdev.android.lib.common.internalapi.android.os

import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import timber.log.Timber

@SuppressLint("PrivateApi")
object SystemPropertiesIA {
    private const val TAG = "SystemBuildPropCompat"

    private var sMtd_get: Method? = null
    private var sMtd_getInt: Method? = null
    private var sMtd_getLong: Method? = null
    private var sMtd_getBoolean: Method? = null

    init {
        try {
            val classObj = Class.forName(
                "android.os.SystemProperties", false,
                Thread.currentThread().contextClassLoader
            )
            sMtd_get = classObj.getMethod("get", String::class.java, String::class.java)
            sMtd_getInt =
                classObj.getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
            sMtd_getLong =
                classObj.getMethod("getLong", String::class.java, Long::class.javaPrimitiveType)
            sMtd_getBoolean = classObj.getMethod(
                "getBoolean",
                String::class.java,
                Boolean::class.javaPrimitiveType
            )
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to reflect SystemProperties")
        }
    }

    fun get(key: String, def: String): String {
        if (sMtd_get != null) {
            try {
                val result = sMtd_get!!.invoke(null, key, def)
                return result as String
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, String)")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, String)")
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, String)")
            }
        } else {
            Timber.tag(TAG).w("#get(String, String) not found")
        }
        return def
    }

    fun getInt(key: String, def: Int): Int {
        if (sMtd_getInt != null) {
            try {
                val result = sMtd_getInt!!.invoke(null, key, def)
                return result as Int
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, int)")
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, int)")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, int)")
            }
        } else {
            Timber.tag(TAG).w("#getInt(String, int) not found")
        }
        return def
    }

    fun getLong(key: String, def: Long): Long {
        if (sMtd_getLong != null) {
            try {
                val result = sMtd_getLong!!.invoke(null, key, def)
                return result as Long
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, long)")
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, long)")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, long)")
            }
        } else {
            Timber.tag(TAG).w("#getLong(String, long) not found")
        }
        return def
    }

    fun getBoolean(key: String, def: Boolean): Boolean {
        if (sMtd_getBoolean != null) {
            try {
                val result = sMtd_getBoolean!!.invoke(null, key, def)
                return result as Boolean
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, boolean)")
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, boolean)")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke get(String, boolean)")
            }
        } else {
            Timber.tag(TAG).w("#getBoolean(String, boolean) not found")
        }
        return def
    }
}
