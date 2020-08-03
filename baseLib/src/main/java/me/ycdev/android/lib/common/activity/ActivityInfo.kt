package me.ycdev.android.lib.common.activity

import android.content.ComponentName

data class ActivityInfo(
    val componentName: ComponentName,
    val taskId: Int,
    var state: State = State.None
) {
    fun makeCopy(): ActivityInfo {
        val cloned = ActivityInfo(componentName, taskId)
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
