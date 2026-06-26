package me.ycdev.android.lib.common.internalapi.android.os

import java.io.IOException
import me.ycdev.android.lib.common.utils.IoUtils
import timber.log.Timber

internal object ProcUtils {
    private const val TAG = "ProcUtils"

    fun readCmdlineProcessName(pid: Int): String? {
        val cmdlineFile = "/proc/$pid/cmdline"
        return try {
            parseCmdlineProcessName(IoUtils.readAllLines(cmdlineFile))
        } catch (e: IOException) {
            Timber.tag(TAG).w(e, "cannot read cmdline file")
            null
        }
    }

    fun parseCmdlineProcessName(cmdline: String): String? {
        val endIndex = cmdline.indexOf('\u0000').let { if (it >= 0) it else cmdline.length }
        return cmdline.substring(0, endIndex).trim().ifEmpty { null }
    }
}
