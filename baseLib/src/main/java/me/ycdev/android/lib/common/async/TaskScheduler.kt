package me.ycdev.android.lib.common.async

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import me.ycdev.android.lib.common.utils.DateTimeUtils
import me.ycdev.android.lib.common.utils.Preconditions
import me.ycdev.android.lib.common.utils.ThreadUtils.isMainThread
import timber.log.Timber
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

class TaskScheduler(private val mTaskExecutor: ITaskExecutor, ownerTag: String) {
    private val mOwnerTag: String
    private var mCheckInterval = DEFAULT_CHECK_INTERVAL
    private var mLogEnabled = false

    private val mMainHandler = MainHandler()
    private val mTasks = ArrayList<TaskInfo>()

    // for test only
    @VisibleForTesting
    internal var mCheckCount: Int = 0

    init {
        Preconditions.checkNotNull(mTaskExecutor)
        Preconditions.checkNotNull(ownerTag)
        mOwnerTag = sTaskSchedulerId.getAndIncrement().toString() + "-" + ownerTag
    }

    fun setCheckInterval(interval: Long) {
        if (interval < 1000) {
            throw IllegalArgumentException("Interval less than 1 second is not allowed.")
        }
        mCheckInterval = interval
    }

    fun enableDebugLogs(enable: Boolean) {
        mLogEnabled = enable
    }

    @JvmOverloads
    fun scheduleAt(
        task: Runnable,
        delayedMs: Long,
        @SchedulePolicy policy: Int = SCHEDULE_POLICY_NO_CHECK
    ) {
        checkSchedulePolicy(policy)
        val taskInfo = TaskInfo(task, delayedMs)
        if (mLogEnabled) {
            Timber.tag(TAG).d(
                "[%s] schedule one-off task: %s, policy: %s",
                mOwnerTag, taskInfo, schedulePolicyToString(policy)
            )
        }
        scheduleTask(taskInfo, policy)
    }

    @JvmOverloads
    fun schedulePeriod(
        task: Runnable,
        delayedMs: Long,
        periodMs: Long,
        @SchedulePolicy policy: Int = SCHEDULE_POLICY_NO_CHECK
    ) {
        checkSchedulePolicy(policy)
        val taskInfo = TaskInfo(task, delayedMs, periodMs)
        if (mLogEnabled) {
            Timber.tag(TAG).d(
                "[%s] schedule period task: %s, policy: %s",
                mOwnerTag, taskInfo, schedulePolicyToString(policy)
            )
        }
        scheduleTask(taskInfo, policy)
    }

    private fun scheduleTask(taskInfo: TaskInfo, @SchedulePolicy policy: Int) {
        if (isMainThread) {
            addTask(taskInfo, policy)
        } else {
            mMainHandler.obtainMessage(MSG_ADD_TASK, policy, 0, taskInfo).sendToTarget()
        }
    }

    fun cancel(task: Runnable) {
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] cancel task: %s", mOwnerTag, task)
        }
        if (isMainThread) {
            removeTask(task)
        } else {
            mMainHandler.obtainMessage(MSG_REMOVE_TASK, task).sendToTarget()
        }
    }

    fun clear() {
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] clear tasks", mOwnerTag)
        }
        if (isMainThread) {
            clearTasks()
        } else {
            mMainHandler.sendEmptyMessage(MSG_CLEAR_TASKS)
        }
    }

    fun trigger() {
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] trigger checking", mOwnerTag)
        }
        if (isMainThread) {
            checkTasks()
        } else {
            mMainHandler.sendEmptyMessage(MSG_CHECK_TASKS)
        }
    }

    @MainThread
    private fun addTask(task: TaskInfo, @SchedulePolicy policy: Int) {
        var taskAdded = false
        if (policy == SCHEDULE_POLICY_NO_CHECK) {
            mTasks.add(task)
            taskAdded = true
        } else {
            val index = findTaskIndex(task.task)
            if (index == -1) {
                mTasks.add(task)
                taskAdded = true
            } else {
                if (mLogEnabled) {
                    Timber.tag(TAG).d("[%s] duplicate task found when add %s", mOwnerTag, task)
                }
                if (policy == SCHEDULE_POLICY_REPLACE) {
                    mTasks[index] = task
                    taskAdded = true
                } // else: nothing to do for ignore
            }
        }

        if (taskAdded) {
            scheduleCheckTask(task.delay)
            if (mLogEnabled) {
                Timber.tag(TAG).d(
                    "[%s] addTask: %s, policy: %s",
                    mOwnerTag, task, schedulePolicyToString(policy)
                )
            }
        }
    }

    @MainThread
    private fun findTaskIndex(task: Runnable): Int {
        for (i in mTasks.indices) {
            val info = mTasks[i]
            if (info.task == task) {
                return i
            }
        }
        return -1
    }

    @MainThread
    private fun removeTask(task: Runnable) {
        var i = 0
        while (i < mTasks.size) {
            val info = mTasks[i]
            if (info.task == task) {
                if (mLogEnabled) {
                    Timber.tag(TAG).d("[%s] task removed: %s", mOwnerTag, info)
                }
                mTasks.removeAt(i)
            } else {
                i++
            }
        } /* empty */
    }

    @MainThread
    private fun checkTasks() {
        mCheckCount++ // for test only
        if (mTasks.isEmpty()) {
            if (mLogEnabled) {
                Timber.tag(TAG).d("[%s] Tasks empty, cancel check.", mOwnerTag)
            }
            mMainHandler.removeMessages(MSG_CHECK_TASKS)
            return
        }

        if (mLogEnabled) {
            Timber.tag(TAG).v("[%s] check tasks, taskCount: %d", mOwnerTag, mTasks.size)
        }
        val it = mTasks.iterator()
        val pendingTasks = ArrayList<Runnable>()
        var nextEventDelay = mCheckInterval
        while (it.hasNext()) {
            var info: TaskInfo? = it.next()
            if (SystemClock.elapsedRealtime() >= info!!.triggerAt) {
                if (mLogEnabled) {
                    Timber.tag(TAG).d("[%s] task to execute: %s", mOwnerTag, info)
                }
                pendingTasks.add(info.task)
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

        if (mLogEnabled) {
            Timber.tag(TAG).v(
                "[%s] next check at %s", mOwnerTag,
                DateTimeUtils.getReadableTimeStamp(System.currentTimeMillis() + nextEventDelay)
            )
        }
        mMainHandler.removeMessages(MSG_CHECK_TASKS)
        mMainHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, nextEventDelay)

        if (pendingTasks.size > 0) {
            mTaskExecutor.postTasks(pendingTasks)
        }
    }

    @MainThread
    private fun clearTasks() {
        mTasks.clear()
        mTaskExecutor.clearTasks()
    }

    @MainThread
    private fun scheduleCheckTask(delay: Long) {
        var delayTmp = delay
        if (delayTmp > mCheckInterval) {
            delayTmp = mCheckInterval
        }
        mMainHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, delayTmp)
    }

    @SuppressLint("HandlerLeak")
    private inner class MainHandler internal constructor() : Handler(Looper.getMainLooper()) {

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
        private val TAG = "TaskScheduler"

        const val SCHEDULE_POLICY_NO_CHECK = 1
        const val SCHEDULE_POLICY_IGNORE = 2
        const val SCHEDULE_POLICY_REPLACE = 3

        private const val MSG_ADD_TASK = 1
        private const val MSG_REMOVE_TASK = 2
        private const val MSG_CHECK_TASKS = 3
        private const val MSG_CLEAR_TASKS = 4

        @VisibleForTesting
        internal val DEFAULT_CHECK_INTERVAL: Long = 10000 // 10 seconds

        private val sTaskSchedulerId = AtomicInteger(1)

        private fun schedulePolicyToString(@SchedulePolicy policy: Int): String {
            when (policy) {
                SCHEDULE_POLICY_NO_CHECK -> return "NO_CHECK"
                SCHEDULE_POLICY_IGNORE -> return "IGNORE"
                SCHEDULE_POLICY_REPLACE -> return "REPLACE"
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

internal class TaskInfo {

    private var taskId: Int = 0
    var task: Runnable
    var delay: Long = 0
    var period: Long = -1
    var triggerAt: Long = 0

    constructor(task: Runnable, delay: Long) {
        this.taskId = sTaskId.getAndIncrement()
        this.task = task
        this.delay = delay
        this.triggerAt = SystemClock.elapsedRealtime() + delay
    }

    constructor(task: Runnable, delay: Long, period: Long) {
        this.taskId = sTaskId.getAndIncrement()
        this.task = task
        this.delay = delay
        this.period = period
        this.triggerAt = SystemClock.elapsedRealtime() + delay
    }

    override fun toString(): String {
        val timestamp = System.currentTimeMillis() - (SystemClock.elapsedRealtime() - triggerAt)
        return ("TaskInfo[id=" + taskId + ", delay=" + delay +
                ", triggerAt=" + DateTimeUtils.getReadableTimeStamp(timestamp) +
                ", period=" + period + "]")
    }

    companion object {
        private val sTaskId = AtomicInteger(1)
    }
}
