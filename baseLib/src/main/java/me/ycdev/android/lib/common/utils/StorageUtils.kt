package me.ycdev.android.lib.common.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import androidx.annotation.WorkerThread
import java.io.File

@Suppress("unused")
object StorageUtils {

    /**
     * Check if the external storage is built-in or removable.
     * @return true if the external storage is removable (like an SD card), false
     * otherwise.
     * @see Environment.isExternalStorageRemovable
     */
    fun isExternalStorageRemovable(): Boolean = Environment.isExternalStorageRemovable()

    /**
     * Check if the external storage is emulated by a portion of the internal storage.
     * @return true if the external storage is emulated, false otherwise.
     * @see Environment.isExternalStorageEmulated
     */
    fun isExternalStorageEmulated(): Boolean = Environment.isExternalStorageEmulated()

    fun isExternalStorageAvailable(): Boolean =
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    @Suppress("DEPRECATION")
    fun getExternalStoragePath(): String = Environment.getExternalStorageDirectory().absolutePath

    /**
     * Returns the number of usable free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File.getUsableSpace
     */
    @WorkerThread
    fun getUsableSpace(path: File, context: Context): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val storageMgr = context.getSystemService(StorageManager::class.java) ?: return 0
            val uuid = storageMgr.getUuidForPath(path)
            return storageMgr.getAllocatableBytes(uuid)
        } else {
            return path.usableSpace
        }
    }

    /**
     * Returns the number of free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File.getFreeSpace
     */
    fun getFreeSpace(path: File): Long {
        return path.freeSpace
    }

    /**
     * Returns the total size in bytes of the partition containing this path.
     * Returns 0 if this path does not exist.
     * @see File.getTotalSpace
     */
    fun getTotalSpace(path: File): Long {
        return path.totalSpace
    }

    /**
     * Get the external app cache directory.
     * @param context The context to use
     * @return The external cache dir
     * @see Context.getExternalCacheDir
     */
    fun getExternalCacheDir(context: Context): File? {
        return context.externalCacheDir
    }
}
