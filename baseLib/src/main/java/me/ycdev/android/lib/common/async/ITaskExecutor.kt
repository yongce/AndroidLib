package me.ycdev.android.lib.common.async

interface ITaskExecutor {
    /**
     * Post a task to execute.
     *
     *
     * This method should return immediately and the task should be executed asynchronously.
     */
    fun postTasks(tasks: List<Runnable>)

    /**
     * Clear all pending tasks.
     */
    fun clearTasks()
}
