package me.ycdev.android.lib.common.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import timber.log.Timber

object ActivityTaskTracker {
    private const val TAG = "ActivityTaskTracker"

    internal val lifecycleCallback = MyLifecycleCallback()

    private val allTasks: HashMap<Int, ActivityTask> = hashMapOf()
    private var focusedTaskId: Int = -1
    private var resumedActivity: Activity? = null

    private var debugLog: Boolean = false

    fun enableDebugLog(enable: Boolean) {
        debugLog = enable
    }

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(lifecycleCallback)
    }

    fun getFocusedTask(): ActivityTask? {
        synchronized(allTasks) {
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
        synchronized(allTasks) {
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

    @GuardedBy("allTasks")
    private fun getOrCreateTaskLocked(taskId: Int): ActivityTask {
        var task = allTasks[taskId]
        if (task == null) {
            task = ActivityTask(taskId)
            allTasks[taskId] = task
        }
        return task
    }

    private fun updateLastActivityState(activity: Activity, state: ActivityInfo.State) {
        synchronized(allTasks) {
            val task = getOrCreateTaskLocked(activity.taskId)
            val appActivity = task.lastActivity(activity.componentName)
            appActivity.state = state
        }
    }

    @VisibleForTesting
    internal class MyLifecycleCallback : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            if (debugLog) Timber.tag(TAG).d("onCreate: %s", activity.componentName)
            synchronized(allTasks) {
                val appActivity = ActivityInfo(activity.componentName, activity.taskId)
                appActivity.state = ActivityInfo.State.Created
                val task = getOrCreateTaskLocked(activity.taskId)
                task.addActivity(appActivity)
            }
        }

        override fun onActivityStarted(activity: Activity) {
            if (debugLog) Timber.tag(TAG).d("onStarted: %s", activity.componentName)
            updateLastActivityState(activity, ActivityInfo.State.Started)
        }

        override fun onActivityResumed(activity: Activity) {
            if (debugLog) Timber.tag(TAG).d("onResumed: %s", activity.componentName)
            updateLastActivityState(activity, ActivityInfo.State.Resumed)
            focusedTaskId = activity.taskId
            resumedActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            if (debugLog) Timber.tag(TAG).d("onPaused: %s", activity.componentName)
            updateLastActivityState(activity, ActivityInfo.State.Paused)
            if (activity == resumedActivity) {
                focusedTaskId = -1
                resumedActivity = null
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
            if (debugLog) Timber.tag(TAG).d("onSaveState: %s", activity.componentName)
        }

        override fun onActivityStopped(activity: Activity) {
            if (debugLog) Timber.tag(TAG).d("onStopped: %s", activity.componentName)
            updateLastActivityState(activity, ActivityInfo.State.Stopped)
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (debugLog) Timber.tag(TAG).d("onDestroyed: %s", activity.componentName)
            synchronized(allTasks) {
                val task = getOrCreateTaskLocked(activity.taskId)
                task.popActivity(activity.componentName)
                if (task.isEmpty()) {
                    allTasks.remove(task.taskId)
                }
            }
        }
    }
}
