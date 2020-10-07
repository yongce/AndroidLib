package me.ycdev.android.lib.common.async

interface ITaskExecutor {
    /**
     * Post a task to execute.
     *
     * This method should return immediately and the task should be executed asynchronously.
     */
    fun postTask(task: Runnable)
}
