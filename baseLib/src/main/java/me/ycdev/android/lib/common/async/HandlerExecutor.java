package me.ycdev.android.lib.common.async;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class HandlerExecutor implements ITaskExecutor {
    private Handler mTaskHandler;

    public HandlerExecutor(@NonNull Looper looper) {
        mTaskHandler = new Handler(looper);
    }

    @Override
    public void postTasks(@NonNull List<Runnable> tasks) {
        for (Runnable task : tasks) {
            mTaskHandler.post(task);
        }
    }

    @Override
    public void clearTasks() {
        mTaskHandler.removeCallbacksAndMessages(null);
    }
}
