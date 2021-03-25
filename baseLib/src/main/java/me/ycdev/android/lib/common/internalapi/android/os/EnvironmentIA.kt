package me.ycdev.android.lib.common.internalapi.android.os

import android.annotation.SuppressLint
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Suppress("unused")
@SuppressLint("PrivateApi")
object EnvironmentIA {
    private const val TAG = "EnvironmentIA"

    private var sMtd_getExternalStorageAndroidDataDir: Method? = null
    private var sMtd_isEncryptedFilesystemEnabled: Method? = null
    private var sMtd_getSecureDataDirectory: Method? = null
    private var sMtd_getSystemSecureDirectory: Method? = null

    init {
        try {
            // API 8: File getExternalStorageAndroidDataDir()
            sMtd_getExternalStorageAndroidDataDir = Environment::class.java.getMethod(
                "getExternalStorageAndroidDataDir"
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "#getExternalStorageAndroidDataDir() not found")
        }

        try {
            // API 9: boolean isEncryptedFilesystemEnabled()
            sMtd_isEncryptedFilesystemEnabled = Environment::class.java.getMethod(
                "isEncryptedFilesystemEnabled"
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "#isEncryptedFilesystemEnabled() not found")
        }

        try {
            // API 9: File getSecureDataDirectory()
            sMtd_getSecureDataDirectory = Environment::class.java.getMethod(
                "getSecureDataDirectory"
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "#getSecureDataDirectory() not found")
        }

        try {
            // API 9: File getSystemSecureDirectory()
            sMtd_getSystemSecureDirectory = Environment::class.java.getMethod(
                "getSystemSecureDirectory"
            )
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "#getSystemSecureDirectory() not found")
        }
    }

    /**
     * Same to the hided method Environment#getExternalStorageAndroidDataDir() (API 8)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    fun getExternalStorageAndroidDataDir(): File? {
        if (sMtd_getExternalStorageAndroidDataDir != null) {
            try {
                return sMtd_getExternalStorageAndroidDataDir!!.invoke(null) as File
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(
                    e, "Failed to invoke #getExternalStorageAndroidDataDir()"
                )
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(
                    e, "Failed to invoke #getExternalStorageAndroidDataDir() ag"
                )
            }
        } else {
            Timber.tag(TAG).w("#getExternalStorageAndroidDataDir() not found")
        }
        return null
    }

    /**
     * Same to the hided Environment#isEncryptedFilesystemEnabled() (API 9)
     */
    fun isEncryptedFilesystemEnabled(): Boolean {
        if (sMtd_isEncryptedFilesystemEnabled != null) {
            try {
                return sMtd_isEncryptedFilesystemEnabled!!.invoke(null) as Boolean
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(
                    e, "Failed to invoke #isEncryptedFilesystemEnabled()"
                )
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(
                    e, "Failed to invoke #isEncryptedFilesystemEnabled() ag"
                )
            }
        } else {
            Timber.tag(TAG).w("#isEncryptedFilesystemEnabled() not found")
        }
        return false
    }

    /**
     * Same to the hided method Environment#getSecureDataDirectory() (API 9)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    fun getSecureDataDirectory(): File? {
        if (sMtd_getSecureDataDirectory != null) {
            try {
                return sMtd_getSecureDataDirectory!!.invoke(null) as File
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getSecureDataDirectory()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getSecureDataDirectory() ag")
            }
        } else {
            Timber.tag(TAG).w("#getSecureDataDirectory() not found")
        }
        return null
    }

    /**
     * Same to the hided method Environment#getSystemSecureDirectory() (API 9)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    fun getSystemSecureDirectory(): File? {
        if (sMtd_getSystemSecureDirectory != null) {
            try {
                return sMtd_getSystemSecureDirectory!!.invoke(null) as File
            } catch (e: IllegalAccessException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getSystemSecureDirectory()")
            } catch (e: InvocationTargetException) {
                Timber.tag(TAG).w(e, "Failed to invoke #getSystemSecureDirectory() ag")
            }
        } else {
            Timber.tag(TAG).w("#getSystemSecureDirectory() not found")
        }
        return null
    }
}
