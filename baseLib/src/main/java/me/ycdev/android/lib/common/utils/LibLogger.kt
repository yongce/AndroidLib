package me.ycdev.android.lib.common.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.RestrictTo
import java.util.Locale

@Suppress("unused")
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object LibLogger {
    private const val TAG = "AndroidLib"
    private var jvmLogger = false

    /**
     * Log enabled by default
     */
    var isLogEnabled: Boolean
        get() = AndroidLogger.isLogEnabled
        set(enabled) {
            if (!jvmLogger) {
                AndroidLogger.isLogEnabled = enabled
            }
        }

    fun enableJvmLogger() {
        jvmLogger = true
    }

    fun setFileLogger(fileLogger: FileLogger) {
        if (!jvmLogger) {
            AndroidLogger.setFileLogger(fileLogger)
        }
    }

    fun v(tag: String, msg: String, vararg args: Any?) {
        log(Log.VERBOSE, tag, null, msg, *args)
    }

    fun d(tag: String, msg: String, vararg args: Any?) {
        log(Log.DEBUG, tag, null, msg, *args)
    }

    fun d(tag: String, e: Throwable, msg: String, vararg args: Any?) {
        log(Log.DEBUG, tag, e, msg, *args)
    }

    fun i(tag: String, msg: String, vararg args: Any?) {
        log(Log.INFO, tag, null, msg, *args)
    }

    fun i(tag: String, e: Throwable, msg: String, vararg args: Any?) {
        log(Log.INFO, tag, e, msg, *args)
    }

    fun w(tag: String, msg: String, vararg args: Any?) {
        log(Log.WARN, tag, null, msg, *args)
    }

    fun w(tag: String, e: Throwable, msg: String, vararg args: Any?) {
        log(Log.WARN, tag, e, msg, *args)
    }

    fun w(tag: String, e: Throwable) {
        log(Log.WARN, tag, e, null)
    }

    fun e(tag: String, msg: String, vararg args: Any?) {
        log(Log.ERROR, tag, null, msg, *args)
    }

    fun e(tag: String, e: Throwable, msg: String, vararg args: Any?) {
        log(Log.ERROR, tag, e, msg, *args)
    }

    fun e(tag: String, e: Throwable) {
        log(Log.ERROR, tag, e, null)
    }

    fun log(level: Int, tag: String, tr: Throwable?, msg: String?, vararg args: Any?) {
        var msgFull = msg
        if (jvmLogger) {
            if (msgFull != null && args.isNotEmpty()) {
                msgFull = String.format(Locale.US, msgFull, *args)
            }
            println("[$tag] $msgFull")
            tr?.printStackTrace()
        } else {
            AndroidLogger.log(level, tag, tr, msgFull, *args)
        }
    }

    private object AndroidLogger {
        /**
         * Log enabled by default
         */
        internal var isLogEnabled = true
            set(enabled) {
                field = enabled
                if (!enabled && fileLogger != null) {
                    fileLogger!!.close()
                }
            }
        private var fileLogger: FileLogger? = null

        fun setFileLogger(fileLogger: FileLogger) {
            this.fileLogger = fileLogger
        }

        fun log(level: Int, tag: String, tr: Throwable?, msg: String?, vararg args: Any?) {
            var msgFull = msg
            if (showLog(level, tag)) {
                if (msgFull != null && args.isNotEmpty()) {
                    msgFull = String.format(Locale.US, msgFull, *args)
                }
                if (tr == null) {
                    Log.println(level, tag, msgFull)
                } else {
                    Log.println(level, tag, msgFull + "\n" + Log.getStackTraceString(tr))
                }
                logToFile(tag, msgFull, tr)
            }
        }

        private fun showLog(level: Int, tag: String): Boolean {
            return isLoggable(tag, level) || isLogEnabled
        }

        @SuppressLint("LogNotTimber")
        private fun isLoggable(tag: String, level: Int): Boolean {
            try {
                return Log.isLoggable(tag, level)
            } catch (e: Exception) {
                if (isLogEnabled) {
                    throw e
                } else {
                    Log.e(TAG, "please check the tag length?", e)
                }
            }
            return false
        }

        private fun logToFile(tag: String, msg: String?, tr: Throwable?) {
            if (isLogEnabled && fileLogger != null) {
                fileLogger!!.logToFile(tag, msg, tr)
            }
        }
    }
}
