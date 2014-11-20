package me.ycdev.android.lib.common.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class StorageUtils {
    /**
     * Returns the number of usable free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File#getUsableSpace()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("deprecation")
    public static long getUsableSpace(File path) {
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
    public static long getFreeSpace(File path) {
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
    public static long getTotalSpace(File path) {
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
        if (AndroidVersionUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Check if the external storage is emulated by a portion of the internal storage.
     * @return true if the external storage is emulated, false otherwise.
     * @see Environment#isExternalStorageEmulated()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isExternalStorageEmulated() {
        if (AndroidVersionUtils.hasHoneycomb()) {
            return Environment.isExternalStorageEmulated();
        }
        return false;
    }

    /**
     * Get the external app cache directory.
     * @param context The context to use
     * @return The external cache dir
     * @see Context#getExternalCacheDir()
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (AndroidVersionUtils.hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

}
