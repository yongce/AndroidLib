package me.ycdev.android.lib.common.async

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.annotation.NonNull

open class HandlerTaskExecutor(@NonNull val taskHandler: Handler) : ITaskExecutor {

    override fun postTask(task: Runnable) {
        taskHandler.post(task)
    }

    companion object {
        fun withMainLooper(): HandlerTaskExecutor {
            return HandlerTaskExecutor(Handler(Looper.getMainLooper()))
        }

        fun withHandlerThread(name: String): HandlerTaskExecutor {
            val thread = HandlerThread(name)
            thread.start()
            return HandlerTaskExecutor(Handler(thread.looper))
        }
    }
}
