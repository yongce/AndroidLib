package me.ycdev.android.lib.common.activity

import android.content.ComponentName

data class ActivityRunningState(
    val componentName: ComponentName,
    val hashCode: Int,
    var taskId: Int,
    var state: State = State.None
) {
    fun makeCopy(): ActivityRunningState {
        val cloned = ActivityRunningState(componentName, hashCode, taskId)
        cloned.state = state
        return cloned
    }

    enum class State {
        None,
        Created,
        Started,
        Resumed,
        Paused,
        Stopped,
        Destroyed
    }
}
