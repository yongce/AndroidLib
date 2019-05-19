package me.ycdev.android.lib.commonjni

object FileStatusHelper {
    init {
        CommonJniLoader.load()
    }

    data class FileStatus(var uid: Int = 0, var gid: Int = 0, var mode: Int = 0)

    external fun getFileStatus(filePath: String): FileStatus
}
