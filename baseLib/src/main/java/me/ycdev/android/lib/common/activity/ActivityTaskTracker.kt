package me.ycdev.android.lib.common.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class can be used to track Activity/task state changes.
 * We can use it in instrumentation test cases to check Activity/task related design/logic.
 */
object ActivityTaskTracker {
    private const val TAG = "ActivityTaskTracker"

    private lateinit var app: Application
    internal val lifecycleCallback = MyLifecycleCallback()

    private val tasksLock = Object()
    @GuardedBy("tasksLock")
    private val allTasks: HashMap<Int, ActivityTask> = hashMapOf()
    @GuardedBy("tasksLock")
    private val activityTaskIds: HashMap<Activity, Int> = hashMapOf()
    @Volatile
    private var totalActivitiesCount = AtomicInteger(0)

    private var focusedTaskId: Int = -1
    private var resumedActivity: Activity? = null

    private var debugLog: Boolean = false

    fun enableDebugLog(enable: Boolean) {
        debugLog = enable
    }

    fun init(app: Application) {
        this.app = app
        app.registerActivityLifecycleCallbacks(lifecycleCallback)
    }

    fun getFocusedTask(): ActivityTask? {
        synchronized(tasksLock) {
            if (focusedTaskId != -1) {
                return allTasks[focusedTaskId]?.makeCopy()
            }
            return null
        }
    }

    /**
     * Return all tasks. The focused task will be the first element in returned list.
     */
    fun getAllTasks(): List<ActivityTask> {
        synchronized(tasksLock) {
            val result = ArrayList<ActivityTask>(allTasks.size)
            // always put the focused task at index 0
            val focusedTask = getFocusedTask()
            if (focusedTask != null) {
                result.add(focusedTask)
            }
            allTasks.values.forEach {
                if (it.taskId != focusedTaskId) {
                    result.add(it.makeCopy())
                }
            }
            return result
        }
    }

    fun getTotalActivitiesCount(): Int {
        return totalActivitiesCount.get()
    }

    @GuardedBy("tasksLock")
    private fun getOrCreateTaskLocked(activity: Activity, taskId: Int): ActivityTask {
        var task = allTasks[taskId]
        if (task == null) {
            val meta = ActivityMeta.get(app, activity.componentName)
            task = ActivityTask(taskId, meta.taskAffinity)
            allTasks[taskId] = task
        }
        return task
    }

    @GuardedBy("tasksLock")
    private fun handleActivityReParentLocked(activity: Activity, taskId: Int) {
        val oldTaskId = activityTaskIds[activity]
        if (oldTaskId != null && oldTaskId != taskId) {
            // the Activity was re-parented, need to handle it
            // Step 1: check if the target task exists
            val newTask = allTasks[taskId]
            if (newTask == null) {
                Timber.tag(TAG).w("Current task list: ")
                allTasks.values.forEach {
                    Timber.tag(TAG).w(it.toString())
                }
                Timber.tag(TAG).w(
                    "But new taskId[%d] found for [%s]",
                    taskId,
                    activity.componentName
                )
                throw RuntimeException("Activity re-parenting error: the new task doesn't exist")
            }

            // Step 2: check if the Activity is in the old task
            val oldTask = allTasks[oldTaskId]
                ?: throw RuntimeException("Activity re-parenting error: the old task doesn't exist")
            val state = oldTask.topActivity()
            if (state.componentName != activity.componentName || state.taskId != oldTaskId) {
                throw RuntimeException("Activity re-parenting error: $state is not matched to ${activity.componentName}")
            }

            // Step 3: (Optional) check Android enforced Activity re-parenting preconditions
            val activityMeta = ActivityMeta.get(app, activity.componentName)
            if (!activityMeta.allowTaskReparenting) {
                throw RuntimeException("Activity re-parenting error: android:allowTaskReparenting was 'false'")
            }
            if (activityMeta.taskAffinity != newTask.taskAffinity) {
                throw RuntimeException("Activity re-parenting error: the Activity's " +
                        "taskAffinity[${activityMeta.taskAffinity}] is not matched with " +
                        "the target task's taskAffinity[${newTask.taskAffinity}]")
            }

            // Step 3: do the re-parenting
            oldTask.popActivity(activity.componentName, activity.hashCode())
            state.taskId = taskId
            newTask.addActivity(state)
            activityTaskIds[activity] = taskId

            if (debugLog) {
                Timber.tag(TAG).d(
                    "[%s] was re-parented from task[%d, %s] to task[%d, %s]",
                    activity.componentName,
                    oldTaskId,
                    oldTask.taskAffinity,
                    newTask.taskId,
                    newTask.taskAffinity
                )
            }
        }
    }

    private fun updateLastActivityState(activity: Activity, taskId: Int, state: ActivityRunningState.State) {
        synchronized(tasksLock) {
            handleActivityReParentLocked(activity, taskId)
            val task = getOrCreateTaskLocked(activity, taskId)
            val appActivity = task.lastActivity(activity.componentName, activity.hashCode())
            appActivity.state = state
        }
    }

    @VisibleForTesting
    internal fun reset() {
        synchronized(tasksLock) {
            allTasks.clear()
            activityTaskIds.clear()
            totalActivitiesCount.set(0)
            focusedTaskId = -1
            resumedActivity = null
        }
    }

    @VisibleForTesting
    internal class MyLifecycleCallback : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onCreate: %s (taskId=%d)", activity.componentName, taskId)
            }
            synchronized(tasksLock) {
                val appActivity = ActivityRunningState(
                    activity.componentName,
                    activity.hashCode(),
                    taskId
                )
                appActivity.state = ActivityRunningState.State.Created
                val task = getOrCreateTaskLocked(activity, taskId)
                task.addActivity(appActivity)
                activityTaskIds[activity] = taskId
                totalActivitiesCount.incrementAndGet()
            }
        }

        override fun onActivityStarted(activity: Activity) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onStarted: %s (taskId=%d)", activity.componentName, taskId)
            }
            updateLastActivityState(activity, taskId, ActivityRunningState.State.Started)
        }

        override fun onActivityResumed(activity: Activity) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onResumed: %s (taskId=%d)", activity.componentName, taskId)
            }
            updateLastActivityState(activity, taskId, ActivityRunningState.State.Resumed)
            focusedTaskId = taskId
            resumedActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onPaused: %s (taskId=%d)", activity.componentName, taskId)
            }
            updateLastActivityState(activity, taskId, ActivityRunningState.State.Paused)
            if (activity == resumedActivity) {
                focusedTaskId = -1
                resumedActivity = null
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onSaveState: %s (taskId=%d)", activity.componentName, taskId)
            }
        }

        override fun onActivityStopped(activity: Activity) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onStopped: %s (taskId=%d)", activity.componentName, taskId)
            }
            updateLastActivityState(activity, taskId, ActivityRunningState.State.Stopped)
        }

        override fun onActivityDestroyed(activity: Activity) {
            val taskId = activity.taskId
            if (debugLog) {
                Timber.tag(TAG).d("onDestroyed: %s (taskId=%d)", activity.componentName, taskId)
            }
            synchronized(tasksLock) {
                val task = getOrCreateTaskLocked(activity, taskId)
                task.popActivity(activity.componentName, activity.hashCode()).apply {
                    state = ActivityRunningState.State.Destroyed
                }
                if (task.isEmpty()) {
                    allTasks.remove(task.taskId)
                }
                totalActivitiesCount.decrementAndGet()
            }
        }
    }
}
