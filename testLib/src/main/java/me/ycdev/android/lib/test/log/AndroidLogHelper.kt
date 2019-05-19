package me.ycdev.android.lib.test.log

@Suppress("MemberVisibilityCanBePrivate")
object AndroidLogHelper {
    // Copy the priority constants from android.util.Log
    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val ASSERT = 7

    fun getPriorityName(priority: Int): String {
        return when (priority) {
            VERBOSE -> "V"
            DEBUG -> "D"
            INFO -> "I"
            WARN -> "W"
            ERROR -> "E"
            ASSERT -> "A"
            else -> "U"
        }
    }
}
