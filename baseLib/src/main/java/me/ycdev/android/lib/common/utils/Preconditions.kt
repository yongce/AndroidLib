package me.ycdev.android.lib.common.utils

object Preconditions {
    fun checkMainThread() {
        if (!ThreadUtils.isMainThread) {
            throw RuntimeException("Not in main thread")
        }
    }

    fun checkNonMainThread() {
        if (ThreadUtils.isMainThread) {
            throw RuntimeException("In main thread")
        }
    }

    fun checkArgument(expression: Boolean) {
        if (!expression) {
            throw IllegalArgumentException()
        }
    }

    fun <T> checkNotNull(obj: T?): T {
        if (obj == null) {
            throw NullPointerException()
        }
        return obj
    }
}
