package me.ycdev.android.lib.common.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

public class StorageUtils {
    /**
     * Returns the number of usable free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getUsableSpace()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("deprecation")
    public static long getUsableSpace(@NonNull File path) {
        if (AndroidVersionUtils.hasGingerbread()) {
            return path.getUsableSpace();
        }
        StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Returns the number of free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getFreeSpace()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("deprecation")
    public static long getFreeSpace(@NonNull File path) {
        if (AndroidVersionUtils.hasGingerbread()) {
            return path.getFreeSpace();
        }
        StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getFreeBlocks();
    }

    /**
     * Returns the total size in bytes of the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getTotalSpace()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("deprecation")
    public static long getTotalSpace(@NonNull File path) {
        if (AndroidVersionUtils.hasGingerbread()) {
            return path.getTotalSpace();
        }
        StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Check if the external storage is built-in or removable.
     * @return true if the external storage is removable (like an SD card), false
     *         otherwise.
     * @see Environment#isExternalStorageRemovable()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        return !AndroidVersionUtils.hasGingerbread() || Environment.isExternalStorageRemovable();
    }

    /**
     * Check if the external storage is emulated by a portion of the internal storage.
     * @return true if the external storage is emulated, false otherwise.
     * @see Environment#isExternalStorageEmulated()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isExternalStorageEmulated() {
        return AndroidVersionUtils.hasHoneycomb() && Environment.isExternalStorageEmulated();
    }

    /**
     * Get the external app cache directory.
     * @param context The context to use
     * @return The external cache dir
     * @see Context#getExternalCacheDir()
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
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
