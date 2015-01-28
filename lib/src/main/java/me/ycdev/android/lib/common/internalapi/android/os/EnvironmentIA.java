package me.ycdev.android.lib.common.internalapi.android.os;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.android.lib.common.utils.LibConfigs;
import me.ycdev.android.lib.common.utils.LibLogger;

import android.os.Environment;

public class EnvironmentIA {
    private static final String TAG = "EnvironmentIA";

    private static Method sMtd_getExternalStorageAndroidDataDir;
    private static Method sMtd_isEncryptedFilesystemEnabled;
    private static Method sMtd_getSecureDataDirectory;
    private static Method sMtd_getSystemSecureDirectory;

    static {
        try {
            // API 8: File getExternalStorageAndroidDataDir()
            sMtd_getExternalStorageAndroidDataDir = Environment.class.getMethod(
                    "getExternalStorageAndroidDataDir");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#getExternalStorageAndroidDataDir() not found", e);
            }
        }

        try {
            // API 9: boolean isEncryptedFilesystemEnabled()
            sMtd_isEncryptedFilesystemEnabled = Environment.class.getMethod(
                    "isEncryptedFilesystemEnabled");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#isEncryptedFilesystemEnabled() not found", e);
            }
        }

        try {
            // API 9: File getSecureDataDirectory()
            sMtd_getSecureDataDirectory = Environment.class.getMethod(
                    "getSecureDataDirectory");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#getSecureDataDirectory() not found", e);
            }
        }

        try {
            // API 9: File getSystemSecureDirectory()
            sMtd_getSystemSecureDirectory = Environment.class.getMethod(
                    "getSystemSecureDirectory");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#getSystemSecureDirectory() not found", e);
            }
        }
    }

    private EnvironmentIA() {
        // nothing to do
    }

    /**
     * Same to the hided method Environment#getExternalStorageAndroidDataDir() (API 8)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    public static File getExternalStorageAndroidDataDir() {
        if (sMtd_getExternalStorageAndroidDataDir != null) {
            try {
                return (File) sMtd_getExternalStorageAndroidDataDir.invoke(null);
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke #getExternalStorageAndroidDataDir()", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke #getExternalStorageAndroidDataDir()", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke #getExternalStorageAndroidDataDir()", e);
            }
        } else {
            LibLogger.w(TAG, "#getExternalStorageAndroidDataDir() not found");
        }
        return null;
    }

    /**
     * Same to the hided Environment#isEncryptedFilesystemEnabled() (API 9)
     */
    public static boolean isEncryptedFilesystemEnabled() {
        if (sMtd_isEncryptedFilesystemEnabled != null) {
            try {
                return (Boolean) sMtd_isEncryptedFilesystemEnabled.invoke(null);
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke #isEncryptedFilesystemEnabled()", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke #isEncryptedFilesystemEnabled()", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke #isEncryptedFilesystemEnabled()", e);
            }
        } else {
            LibLogger.w(TAG, "#isEncryptedFilesystemEnabled() not found");
        }
        return false;
    }

    /**
     * Same to the hided method Environment#getSecureDataDirectory() (API 9)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    public static File getSecureDataDirectory() {
        if (sMtd_getSecureDataDirectory != null) {
            try {
                return (File) sMtd_getSecureDataDirectory.invoke(null);
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke #getSecureDataDirectory()", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke #getSecureDataDirectory()", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke #getSecureDataDirectory()", e);
            }
        } else {
            LibLogger.w(TAG, "#getSecureDataDirectory() not found");
        }
        return null;
    }

    /**
     * Same to the hided method Environment#getSystemSecureDirectory() (API 9)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    public static File getSystemSecureDirectory() {
        if (sMtd_getSystemSecureDirectory != null) {
            try {
                return (File) sMtd_getSystemSecureDirectory.invoke(null);
            } catch (IllegalArgumentException e) {
                LibLogger.w(TAG, "Failed to invoke #getSystemSecureDirectory()", e);
            } catch (IllegalAccessException e) {
                LibLogger.w(TAG, "Failed to invoke #getSystemSecureDirectory()", e);
            } catch (InvocationTargetException e) {
                LibLogger.w(TAG, "Failed to invoke #getSystemSecureDirectory()", e);
            }
        } else {
            LibLogger.w(TAG, "#getSystemSecureDirectory() not found");
        }
        return null;
    }
}
