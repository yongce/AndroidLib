package me.ycdev.android.lib.common.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

@SuppressWarnings({"unused", "WeakerAccess"})
public class StorageUtils {
    /**
     * Returns the number of usable free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getUsableSpace()
     */
    @SuppressWarnings("deprecation")
    public static long getUsableSpace(@NonNull File path) {
        return path.getUsableSpace();
    }

    /**
     * Returns the number of free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getFreeSpace()
     */
    @SuppressWarnings("deprecation")
    public static long getFreeSpace(@NonNull File path) {
        return path.getFreeSpace();
    }

    /**
     * Returns the total size in bytes of the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getTotalSpace()
     */
    @SuppressWarnings("deprecation")
    public static long getTotalSpace(@NonNull File path) {
        return path.getTotalSpace();
    }

    /**
     * Check if the external storage is built-in or removable.
     * @return true if the external storage is removable (like an SD card), false
     *         otherwise.
     * @see Environment#isExternalStorageRemovable()
     */
    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    /**
     * Check if the external storage is emulated by a portion of the internal storage.
     * @return true if the external storage is emulated, false otherwise.
     * @see Environment#isExternalStorageEmulated()
     */
    public static boolean isExternalStorageEmulated() {
        return Environment.isExternalStorageEmulated();
    }

    /**
     * Get the external app cache directory.
     * @param context The context to use
     * @return The external cache dir
     * @see Context#getExternalCacheDir()
     */
    public static File getExternalCacheDir(@NonNull Context context) {
        return context.getExternalCacheDir();
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
