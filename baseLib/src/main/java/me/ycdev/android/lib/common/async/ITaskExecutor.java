package me.ycdev.android.lib.common.async;

import android.support.annotation.NonNull;

import java.util.List;

public interface ITaskExecutor {
    /**
     * Post a task to execute.
     * <p />
     * This method should return immediately and the task should be executed asynchronously.
     */
    void postTasks(@NonNull List<Runnable> tasks);

    /**
     * Clear all pending tasks.
     */
    void clearTasks();
}
