package me.ycdev.android.lib.test.log

import java.util.ArrayList
import timber.log.Timber

@Suppress("unused")
class TimberJvmTree : Timber.Tree() {
    private var logs: ArrayList<String>? = null

    fun clear() {
        logs?.clear()
    }

    fun hasLogs(): Boolean {
        return logs?.isNotEmpty() ?: false
    }

    fun keepLogs() {
        logs = ArrayList()
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val log = AndroidLogHelper.getPriorityName(priority) + "/" + tag + ": " + message
        logs?.add(log)
        println(log)
        t?.printStackTrace(System.out)
    }

    companion object {
        fun plantIfNeeded() {
            // only plant TimberJvmTree once
            Timber.forest().forEach {
                if (it is TimberJvmTree) {
                    return
                }
            }
            Timber.plant(TimberJvmTree())
        }
    }
}
