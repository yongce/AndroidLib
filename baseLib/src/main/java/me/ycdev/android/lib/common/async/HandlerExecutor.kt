package me.ycdev.android.lib.common.async

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

open class HandlerExecutor(looper: Looper) : ITaskExecutor {
    private val taskHandler: Handler = Handler(looper)

    override fun postTasks(tasks: List<Runnable>) {
        for (task in tasks) {
            taskHandler.post(task)
        }
    }

    override fun clearTasks() {
        taskHandler.removeCallbacksAndMessages(null)
    }

    companion object {
        fun withMainLooper(): HandlerExecutor {
            return HandlerExecutor(Looper.getMainLooper())
        }

        fun withHandlerThread(name: String): HandlerExecutor {
            val thread = HandlerThread(name)
            thread.start()
            return HandlerExecutor(thread.looper)
        }
    }
}
