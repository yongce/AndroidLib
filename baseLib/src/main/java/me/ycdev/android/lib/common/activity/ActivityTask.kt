package me.ycdev.android.lib.common.activity

import android.content.ComponentName
import java.util.Stack

class ActivityTask(val taskId: Int, val taskAffinity: String) {
    private val activities = arrayListOf<ActivityRunningState>()

    internal fun addActivity(activity: ActivityRunningState) {
        if (activity.taskId != taskId) {
            throw RuntimeException("Activity taskId[${activity.taskId}] != AppTask[$taskId]")
        }
        activities.add(activity)
    }

    internal fun popActivity(componentName: ComponentName, hashCode: Int): ActivityRunningState {
        val it = activities.asReversed().iterator()
        while (it.hasNext()) {
            val activity = it.next()
            if (activity.componentName == componentName && activity.hashCode == hashCode) {
                it.remove()
                return activity
            }
        }
        val hashHex = Integer.toHexString(hashCode)
        throw RuntimeException("Cannot find $componentName@$hashHex")
    }

    fun lastActivity(componentName: ComponentName, hashCode: Int): ActivityRunningState {
        activities.asReversed().forEach {
            if (it.componentName == componentName && it.hashCode == hashCode) {
                return it
            }
        }
        val hashHex = Integer.toHexString(hashCode)
        throw RuntimeException("Cannot find $componentName@$hashHex")
    }

    fun topActivity(): ActivityRunningState {
        if (activities.isEmpty()) {
            throw RuntimeException("The task is empty. Cannot get the top Activity.")
        }
        return activities[activities.lastIndex]
    }

    /**
     * @return The last Activity in returned list is the top Activity
     */
    fun getActivityStack(): Stack<ActivityRunningState> {
        val stack = Stack<ActivityRunningState>()
        activities.forEach {
            stack.push(it)
        }
        return stack
    }

    fun isEmpty() = activities.isEmpty()

    fun makeCopy(): ActivityTask {
        val task = ActivityTask(taskId, taskAffinity)
        activities.forEach {
            task.activities.add(it.makeCopy())
        }
        return task
    }

    override fun toString(): String {
        return "AppTask[taskId=$taskId, taskAffinity=$taskAffinity, activities=$activities]"
    }
}
