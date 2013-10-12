package me.ycdev.androidlib.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.os.Build;
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

}
