package me.ycdev.android.lib.common.activity

import android.content.ComponentName
import java.util.Stack

class ActivityTask(val taskId: Int) {
    private val activities = arrayListOf<ActivityInfo>()

    internal fun addActivity(activity: ActivityInfo) {
        if (activity.taskId != taskId) {
            throw RuntimeException("Activity taskId[${activity.taskId}] != AppTask[$taskId]")
        }
        activities.add(activity)
    }

    internal fun popActivity(componentName: ComponentName): ActivityInfo {
        val it = activities.asReversed().iterator()
        while (it.hasNext()) {
            val activity = it.next()
            if (activity.componentName == componentName) {
                it.remove()
                return activity
            }
        }
        throw RuntimeException("Cannot find $componentName")
    }

    fun lastActivity(componentName: ComponentName): ActivityInfo {
        activities.asReversed().forEach {
            if (it.componentName == componentName) {
                return it
            }
        }
        throw RuntimeException("Cannot find $componentName")
    }

    fun topActivity(): ActivityInfo {
        if (activities.isEmpty()) {
            throw RuntimeException("The task is empty. Cannot get the top Activity.")
        }
        return activities[activities.lastIndex]
    }

    /**
     * @return The last Activity in returned list is the top Activity
     */
    fun getActivityStack(): Stack<ActivityInfo> {
        val stack = Stack<ActivityInfo>()
        activities.forEach {
            stack.push(it)
        }
        return stack
    }

    fun isEmpty() = activities.isEmpty()

    fun makeCopy(): ActivityTask {
        val task = ActivityTask(taskId)
        activities.forEach {
            task.activities.add(it.makeCopy())
        }
        return task
    }

    override fun toString(): String {
        return "AppTask[taskId=$taskId, activities=$activities]"
    }
}
