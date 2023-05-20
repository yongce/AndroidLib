package me.ycdev.android.lib.common.async

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import me.ycdev.android.lib.common.utils.Preconditions
import timber.log.Timber

/**
 * An utility class for processing tasks async. It's similar to [android.app.IntentService]
 * and has following features:
 *  * 1. All tasks are executed one-by-one in a worker thread by [Handler].
 *  * 2. The worker thread is created when needed, and destroyed when not needed anymore.
 * Also, you can customize the delay time for the thread's auto destroying.
 *
 *
 * Because of the background limits in Android O, we cannot use [android.app.IntentService]
 * anymore in background (if the target API is set to Android O or higher versions).
 * This class may be a possible replacement for it.
 */
class AsyncTaskQueue(private val name: String) {
    private var autoQuitDelay = WORKER_THREAD_AUTO_QUIT_DELAY_DEFAULT

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    internal var taskHandler: Handler? = null
        private set

    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (DEV_LOG) Timber.tag(TAG).d("MainHandler#handleMessage: %s", msg)
            if (msg.what == MSG_MAIN_NEW_TASK) {
                val params = msg.obj as TaskParams
                prepareForNewTask()
                val taskMessage = taskHandler!!.obtainMessage(MSG_WORKER_NEW_TASK, params.task)
                if (params.delay > 0) {
                    taskHandler!!.sendMessageDelayed(taskMessage, params.delay)
                } else {
                    taskHandler!!.sendMessage(taskMessage)
                }
            } else if (msg.what == MSG_MAIN_REMOVE_TASK) {
                val task = msg.obj as Runnable
                prepareForNewTask()
                taskHandler!!.removeMessages(MSG_WORKER_NEW_TASK, task)
            } else if (msg.what == MSG_MAIN_WORKER_THREAD_QUIT) {
                Timber.tag(TAG).d("task thread quiting")
                taskHandler!!.looper.quit()
                taskHandler = null
            }
        }
    }

    private val taskCallback = Handler.Callback { msg ->
        if (DEV_LOG) Timber.tag(TAG).d("TaskHandler#handleMessage: %s", msg)
        if (msg.what == MSG_WORKER_NEW_TASK) {
            // Execute the task
            val task = msg.obj as Runnable
            task.run()

            // Post a cleaner task!
            // Don't need to check null. If that happens, there MUST be bugs.
            taskHandler!!.removeMessages(MSG_WORKER_THREAD_QUIT)
            if (autoQuitDelay > 0) {
                taskHandler!!.sendEmptyMessageDelayed(MSG_WORKER_THREAD_QUIT, autoQuitDelay)
            } else {
                taskHandler!!.sendEmptyMessage(MSG_WORKER_THREAD_QUIT)
            }
        } else if (msg.what == MSG_WORKER_THREAD_QUIT) {
            mainHandler.sendEmptyMessage(MSG_MAIN_WORKER_THREAD_QUIT)
        } else {
            return@Callback false
        }

        true
    }

    fun setWorkerThreadAutoQuitDelay(delay: Long) {
        autoQuitDelay = if (delay < WORKER_THREAD_AUTO_QUIT_DELAY_MIN) {
            Timber.tag(TAG).w(
                "Ignore the requested delay [%d]. Set it to the minimum value [%d].",
                delay, WORKER_THREAD_AUTO_QUIT_DELAY_MIN
            )
            WORKER_THREAD_AUTO_QUIT_DELAY_MIN
        } else {
            delay
        }
    }

    fun addTask(delay: Long, task: Runnable) {
        if (DEV_LOG) Timber.tag(TAG).d("addTask: %s, delay: %d", task, delay)
        val params = TaskParams(task, delay)
        mainHandler.obtainMessage(MSG_MAIN_NEW_TASK, params).sendToTarget()
    }

    fun addTask(task: Runnable) {
        addTask(0L, task)
    }

    fun removeTask(task: Runnable) {
        if (DEV_LOG) Timber.tag(TAG).d("removeTask: %s", task)
        mainHandler.obtainMessage(MSG_MAIN_REMOVE_TASK, task).sendToTarget()
    }

    @MainThread
    private fun setupTaskHandler() {
        Preconditions.checkMainThread()
        if (taskHandler == null) {
            Timber.tag(TAG).d("Creating task thread")
            val thread = HandlerThread(name)
            thread.start()
            taskHandler = Handler(thread.looper, taskCallback)
        }
    }

    @MainThread
    private fun prepareForNewTask() {
        mainHandler.removeMessages(MSG_MAIN_WORKER_THREAD_QUIT)
        setupTaskHandler()
        taskHandler!!.removeMessages(MSG_WORKER_THREAD_QUIT)
    }

    private class TaskParams(
        var task: Runnable,
        var delay: Long
    )

    companion object {
        private const val TAG = "AsyncTaskQueue"
        private const val DEV_LOG = false

        private const val MSG_MAIN_NEW_TASK = 1
        private const val MSG_MAIN_REMOVE_TASK = 2
        private const val MSG_MAIN_WORKER_THREAD_QUIT = 3

        private const val MSG_WORKER_NEW_TASK = 11
        private const val MSG_WORKER_THREAD_QUIT = 12

        const val WORKER_THREAD_AUTO_QUIT_DELAY_MIN = 10 * 1000L // 10 seconds
        const val WORKER_THREAD_AUTO_QUIT_DELAY_DEFAULT = 30 * 1000L // 30 seconds
    }
}
