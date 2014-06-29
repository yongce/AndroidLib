package me.ycdev.androidlib.internalapi.android.os;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.ycdev.androidlib.LibConfigs;
import me.ycdev.androidlib.utils.LibLogger;

import android.os.Environment;

public class EnvironmentIA {
    private static final String TAG = "EnvironmentIA";

    private static Method sMtdGetExternalStorageAndroidDataDir;
    private static Method sMtdIsEncryptedFilesystemEnabled;
    private static Method sMtdGetSecureDataDirectory;
    private static Method sMtdGetSystemSecureDirectory;

    static {
        try {
            // API 8: File getExternalStorageAndroidDataDir()
            sMtdGetExternalStorageAndroidDataDir = Environment.class.getMethod(
                    "getExternalStorageAndroidDataDir");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#getExternalStorageAndroidDataDir() not found", e);
            }
        }

        try {
            // API 9: boolean isEncryptedFilesystemEnabled()
            sMtdIsEncryptedFilesystemEnabled = Environment.class.getMethod(
                    "isEncryptedFilesystemEnabled");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#isEncryptedFilesystemEnabled() not found", e);
            }
        }

        try {
            // API 9: File getSecureDataDirectory()
            sMtdGetSecureDataDirectory = Environment.class.getMethod(
                    "getSecureDataDirectory");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#getSecureDataDirectory() not found", e);
            }
        }

        try {
            // API 9: File getSystemSecureDirectory()
            sMtdGetSystemSecureDirectory = Environment.class.getMethod(
                    "getSystemSecureDirectory");
        } catch (NoSuchMethodException e) {
            if (LibConfigs.DEBUG_LOG) {
                LibLogger.w(TAG, "#getSystemSecureDirectory() not found", e);
            }
        }
    }

    /**
     * Same to the hided method Environment#getExternalStorageAndroidDataDir() (API 8)
     * @return null may be returned if the method not supported or failed to invoke it
     */
    public static File getExternalStorageAndroidDataDir() {
        if (sMtdGetExternalStorageAndroidDataDir != null) {
            try {
                return (File) sMtdGetExternalStorageAndroidDataDir.invoke(null);
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
        if (sMtdIsEncryptedFilesystemEnabled != null) {
            try {
                return (Boolean) sMtdIsEncryptedFilesystemEnabled.invoke(null);
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
        if (sMtdGetSecureDataDirectory != null) {
            try {
                return (File) sMtdGetSecureDataDirectory.invoke(null);
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
        if (sMtdGetSystemSecureDirectory != null) {
            try {
                return (File) sMtdGetSystemSecureDirectory.invoke(null);
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
