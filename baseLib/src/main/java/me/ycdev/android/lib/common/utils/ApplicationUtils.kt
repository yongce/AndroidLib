package me.ycdev.android.lib.common.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import java.io.IOException

@Suppress("unused", "MemberVisibilityCanBePrivate")
object ApplicationUtils {
    private const val TAG = "ApplicationUtils"

    @SuppressLint("StaticFieldLeak")
    private lateinit var app: Application
    private var processName: String? = null

    val application: Application
        get() {
            Preconditions.checkNotNull(app)
            return app
        }

    // try AMS first
    // try "/proc"
    val currentProcessName: String?
        get() {
            Preconditions.checkNotNull(app)

            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
            val pid = Process.myPid()
            processName = getProcessNameFromAMS(app, pid)
            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
            processName = getProcessNameFromProc(pid)
            return processName
        }

    /**
     * Must be called in Application#onCreate() ASAP.
     */
    fun initApplication(app: Application) {
        this.app = app
    }

    private fun getProcessNameFromAMS(cxt: Context, pid: Int): String? {
        val am = SystemServiceHelper.getActivityManager(cxt) ?: return null
        val runningApps = SystemServiceHelper.getRunningAppProcesses(am)
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }

    private fun getProcessNameFromProc(pid: Int): String? {
        var processName: String? = null
        try {
            val cmdlineFile = "/proc/$pid/cmdline"
            processName = IoUtils.readAllLines(cmdlineFile)
        } catch (e: IOException) {
            LibLogger.w(TAG, "failed to read process name from /proc for pid [%d]", pid)
        }

        if (processName != null) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    }
}
