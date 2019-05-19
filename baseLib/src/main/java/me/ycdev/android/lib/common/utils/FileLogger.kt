package me.ycdev.android.lib.common.utils

import android.annotation.SuppressLint
import android.os.Process
import android.text.TextUtils
import android.util.Log
import androidx.annotation.GuardedBy

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLogger constructor(
    private val logDir: String?,
    private val logFileNamePrefix: String,
    private val processNameSuffix: String? = null
) {

    private var fileWriter: Writer? = null
    // Each log file every day.
    private var currentDay: String? = null

    private val dayFormat = SimpleDateFormat("yyMMdd", Locale.US)
    private val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss:SSS", Locale.US)

    private fun getCurrentDay(): String = dayFormat.format(Date())

    @GuardedBy("this")
    @Synchronized
    fun close() {
        IoUtils.closeQuietly(fileWriter)
        fileWriter = null
    }

    @GuardedBy("this")
    fun logToFile(tag: String, msg: String?, tr: Throwable?) {
        val builder = StringBuilder()
        builder.append(timeFormat.format(Date()))
        builder.append(" ")
        builder.append(tag)
        builder.append("\t")
        builder.append(Process.myPid()).append(" ").append(Process.myTid()).append(" ")
        if (!TextUtils.isEmpty(msg)) {
            builder.append(msg)
        }
        if (tr != null) {
            builder.append("\n\t")
            builder.append(Log.getStackTraceString(tr))
        }
        builder.append("\n")

        writeLog(builder.toString())
    }

    @GuardedBy("this")
    @Synchronized
    private fun writeLog(logLine: String) {
        if (null == fileWriter) {
            if (!openFile()) {
                return
            }
        }

        try {
            val day = getCurrentDay()
            // If is another day, then create a new log file.
            if (day != currentDay) {
                fileWriter!!.flush()
                fileWriter!!.close()
                fileWriter = null

                val success = openFile()
                if (!success) {
                    return
                }
            }

            fileWriter!!.write(logLine)
            fileWriter!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("LogNotTimber")
    @GuardedBy("this")
    private fun openFile(): Boolean {
        if (logDir == null) {
            return false
        }

        val logDirFile = File(logDir)
        if (!logDirFile.exists()) {
            if (!logDirFile.mkdirs()) {
                Log.w(TAG, "Cannot create dir: $logDir")
                return false
            }
        }

        currentDay = getCurrentDay()
        try {
            val logFile = File(logDir, composeFileName(currentDay))
            fileWriter = FileWriter(logFile, true)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    private fun composeFileName(currentDay: String?): String {
        val sb = StringBuilder()
        sb.append(logFileNamePrefix).append("_log_").append(currentDay)
        if (!TextUtils.isEmpty(processNameSuffix)) {
            sb.append("_").append(processNameSuffix)
        }
        sb.append(".txt")
        return sb.toString()
    }

    companion object {
        private const val TAG = "FileLogger"
    }
}
