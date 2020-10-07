package me.ycdev.android.lib.common.async

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.VisibleForTesting
import me.ycdev.android.lib.common.annotation.HandlerWork
import me.ycdev.android.lib.common.utils.DateTimeUtils
import me.ycdev.android.lib.common.utils.Preconditions
import timber.log.Timber
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

class TaskScheduler(@NonNull schedulerLooper: Looper, @NonNull ownerTag: String) {
    private val ownerTag: String = taskSchedulerIdGenerator.incrementAndGet().toString() + "-" + ownerTag
    private var checkInterval = DEFAULT_CHECK_INTERVAL
    private var logEnabled = false

    private val schedulerHandler = SchedulerHandler(schedulerLooper)
    private val tasks = ArrayList<TaskInfo>()

    // for test only
    @VisibleForTesting
    internal var checkCount: Int = 0

    init {
        Preconditions.checkNotNull(schedulerLooper)
        Preconditions.checkNotNull(ownerTag)
    }

    fun setCheckInterval(interval: Long) {
        if (interval < 1000) {
            throw IllegalArgumentException("Interval less than 1 second is not allowed.")
        }
        checkInterval = interval
    }

    fun enableDebugLogs(enable: Boolean) {
        logEnabled = enable
    }

    fun schedule(executor: ITaskExecutor, delayedMs: Long, task: Runnable) {
        schedule(executor, delayedMs, SCHEDULE_POLICY_NO_CHECK, task)
    }

    fun schedule(
        executor: ITaskExecutor,
        delayedMs: Long,
        @SchedulePolicy policy: Int,
        task: Runnable,
    ) {
        checkSchedulePolicy(policy)
        val taskInfo = TaskInfo(executor, task, delayedMs)
        if (logEnabled) {
            Timber.tag(TAG).d(
                "[%s] schedule one-off task: %s, policy: %s",
                ownerTag, taskInfo, schedulePolicyToString(policy)
            )
        }
        scheduleTask(taskInfo, policy)
    }

    fun schedulePeriod(executor: ITaskExecutor, delayedMs: Long, periodMs: Long, task: Runnable) {
        schedulePeriod(executor, delayedMs, periodMs, SCHEDULE_POLICY_NO_CHECK, task)
    }

    fun schedulePeriod(
        executor: ITaskExecutor,
        delayedMs: Long,
        periodMs: Long,
        @SchedulePolicy policy: Int,
        task: Runnable,
    ) {
        checkSchedulePolicy(policy)
        val taskInfo = TaskInfo(executor, task, delayedMs, periodMs)
        if (logEnabled) {
            Timber.tag(TAG).d(
                "[%s] schedule period task: %s, policy: %s",
                ownerTag, taskInfo, schedulePolicyToString(policy)
            )
        }
        scheduleTask(taskInfo, policy)
    }

    private fun scheduleTask(taskInfo: TaskInfo, @SchedulePolicy policy: Int) {
        if (Looper.myLooper() == schedulerHandler.looper) {
            addTask(taskInfo, policy)
        } else {
            schedulerHandler.obtainMessage(MSG_ADD_TASK, policy, 0, taskInfo).sendToTarget()
        }
    }

    fun cancel(task: Runnable) {
        if (logEnabled) {
            Timber.tag(TAG).d("[%s] cancel task: %s", ownerTag, task)
        }
        if (Looper.myLooper() == schedulerHandler.looper) {
            removeTask(task)
        } else {
            schedulerHandler.obtainMessage(MSG_REMOVE_TASK, task).sendToTarget()
        }
    }

    fun clear() {
        if (logEnabled) {
            Timber.tag(TAG).d("[%s] clear tasks", ownerTag)
        }
        if (Looper.myLooper() == schedulerHandler.looper) {
            clearTasks()
        } else {
            schedulerHandler.sendEmptyMessage(MSG_CLEAR_TASKS)
        }
    }

    fun trigger() {
        if (logEnabled) {
            Timber.tag(TAG).d("[%s] trigger checking", ownerTag)
        }
        if (Looper.myLooper() == schedulerHandler.looper) {
            checkTasks()
        } else {
            schedulerHandler.sendEmptyMessage(MSG_CHECK_TASKS)
        }
    }

    @HandlerWork("schedulerHandler")
    private fun addTask(task: TaskInfo, @SchedulePolicy policy: Int) {
        var taskAdded = false
        if (policy == SCHEDULE_POLICY_NO_CHECK) {
            tasks.add(task)
            taskAdded = true
        } else {
            val index = findTaskIndex(task.task)
            if (index == -1) {
                tasks.add(task)
                taskAdded = true
            } else {
                if (logEnabled) {
                    Timber.tag(TAG).d("[%s] duplicate task found when add %s", ownerTag, task)
                }
                if (policy == SCHEDULE_POLICY_REPLACE) {
                    tasks[index] = task
                    taskAdded = true
                } // else: nothing to do for ignore
            }
        }

        if (taskAdded) {
            scheduleCheckTask(task.delay)
            if (logEnabled) {
                Timber.tag(TAG).d(
                    "[%s] addTask: %s, policy: %s",
                    ownerTag, task, schedulePolicyToString(policy)
                )
            }
        }
    }

    @HandlerWork("schedulerHandler")
    private fun findTaskIndex(task: Runnable): Int {
        for (i in tasks.indices) {
            val info = tasks[i]
            if (info.task == task) {
                return i
            }
        }
        return -1
    }

    @HandlerWork("schedulerHandler")
    private fun removeTask(task: Runnable) {
        var i = 0
        while (i < tasks.size) {
            val info = tasks[i]
            if (info.task == task) {
                if (logEnabled) {
                    Timber.tag(TAG).d("[%s] task removed: %s", ownerTag, info)
                }
                tasks.removeAt(i)
            } else {
                i++
            }
        } /* empty */
    }

    @HandlerWork("schedulerHandler")
    private fun checkTasks() {
        checkCount++ // for test only
        if (tasks.isEmpty()) {
            if (logEnabled) {
                Timber.tag(TAG).d("[%s] Tasks empty, cancel check.", ownerTag)
            }
            schedulerHandler.removeMessages(MSG_CHECK_TASKS)
            return
        }

        if (logEnabled) {
            Timber.tag(TAG).v("[%s] check tasks, taskCount: %d", ownerTag, tasks.size)
        }
        val it = tasks.iterator()
        val pendingTasks = ArrayList<TaskInfo>()
        var nextEventDelay = checkInterval
        while (it.hasNext()) {
            var info: TaskInfo? = it.next()
            if (SystemClock.elapsedRealtime() >= info!!.triggerAt) {
                if (logEnabled) {
                    Timber.tag(TAG).d("[%s] task to execute: %s", ownerTag, info)
                }
                pendingTasks.add(info)
                if (info.period > 0) {
                    info.triggerAt = SystemClock.elapsedRealtime() + info.period
                } else {
                    it.remove()
                    info = null // mark it removed from queue
                }
            }

            if (info != null) {
                val timeout = info.triggerAt - SystemClock.elapsedRealtime()
                if (timeout < nextEventDelay) {
                    nextEventDelay = timeout
                }
            }
        }

        if (logEnabled) {
            Timber.tag(TAG).v(
                "[%s] next check at %s", ownerTag,
                DateTimeUtils.getReadableTimeStamp(System.currentTimeMillis() + nextEventDelay)
            )
        }
        schedulerHandler.removeMessages(MSG_CHECK_TASKS)
        schedulerHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, nextEventDelay)

        for (info in pendingTasks) {
            info.executor.postTask(info.task)
        }
    }

    @HandlerWork("schedulerHandler")
    private fun clearTasks() {
        tasks.clear()
    }

    @HandlerWork("schedulerHandler")
    private fun scheduleCheckTask(delay: Long) {
        var delayTmp = delay
        if (delayTmp > checkInterval) {
            delayTmp = checkInterval
        }
        schedulerHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, delayTmp)
    }

    @SuppressLint("HandlerLeak")
    private inner class SchedulerHandler(@NonNull looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_ADD_TASK -> {
                    val task = msg.obj as TaskInfo
                    val policy = msg.arg1
                    addTask(task, policy)
                }

                MSG_REMOVE_TASK -> {
                    val task = msg.obj as Runnable
                    removeTask(task)
                }

                MSG_CHECK_TASKS -> {
                    checkTasks()
                }

                MSG_CLEAR_TASKS -> {
                    clearTasks()
                }
            }
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(SCHEDULE_POLICY_NO_CHECK, SCHEDULE_POLICY_IGNORE, SCHEDULE_POLICY_REPLACE)
    annotation class SchedulePolicy

    companion object {
        private const val TAG = "TaskScheduler"

        const val SCHEDULE_POLICY_NO_CHECK = 1
        const val SCHEDULE_POLICY_IGNORE = 2
        const val SCHEDULE_POLICY_REPLACE = 3

        private const val MSG_ADD_TASK = 1
        private const val MSG_REMOVE_TASK = 2
        private const val MSG_CHECK_TASKS = 3
        private const val MSG_CLEAR_TASKS = 4

        @VisibleForTesting
        internal const val DEFAULT_CHECK_INTERVAL: Long = 10_000 // 10 seconds

        private val taskSchedulerIdGenerator = AtomicInteger(0)

        private fun schedulePolicyToString(@SchedulePolicy policy: Int): String {
            return when (policy) {
                SCHEDULE_POLICY_NO_CHECK -> "NO_CHECK"
                SCHEDULE_POLICY_IGNORE -> "IGNORE"
                SCHEDULE_POLICY_REPLACE -> "REPLACE"
                else -> throw RuntimeException("Unknown policy: $policy")
            }
        }

        private fun checkSchedulePolicy(@SchedulePolicy policy: Int) {
            when (policy) {
                SCHEDULE_POLICY_NO_CHECK,
                SCHEDULE_POLICY_IGNORE,
                SCHEDULE_POLICY_REPLACE -> return
                else -> throw RuntimeException("Unknown policy: $policy")
            }
        }
    }
}

