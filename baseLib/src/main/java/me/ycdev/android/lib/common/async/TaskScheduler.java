package me.ycdev.android.lib.common.async;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import me.ycdev.android.lib.common.utils.DateTimeUtils;
import me.ycdev.android.lib.common.utils.Preconditions;
import timber.log.Timber;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TaskScheduler {
    private static final String TAG = "TaskScheduler";

    private static final int MSG_ADD_TASK = 1;
    private static final int MSG_REMOVE_TASK = 2;
    private static final int MSG_CHECK_TASKS = 3;
    private static final int MSG_CLEAR_TASKS = 4;

    private static final int DEFAULT_CHECK_INTERVAL = 10_000; // 10 seconds

    private ITaskExecutor mTaskExecutor;
    private String mOwnerTag;
    private long mCheckInterval = DEFAULT_CHECK_INTERVAL;

    private Handler mMainHandler = new MainHandler();
    private ArrayList<TaskInfo> mTasks = new ArrayList<>();

    public TaskScheduler(@NonNull ITaskExecutor executor, @NonNull String ownerTag) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(ownerTag);
        mTaskExecutor = executor;
        mOwnerTag = ownerTag;
    }

    public void setCheckInterval(long interval) {
        if (interval < 1000) {
            throw new IllegalArgumentException("Interval less than 1 second is not allowed.");
        }
        mCheckInterval = interval;
    }

    public void schedule(@NonNull Runnable task, long delayedMs) {
        TaskInfo taskInfo = new TaskInfo(task, delayedMs);
        Timber.tag(TAG).d("[%s] schedule one-off task: %s", mOwnerTag, taskInfo);
        mMainHandler.obtainMessage(MSG_ADD_TASK, taskInfo).sendToTarget();
    }

    public void schedule(@NonNull Runnable task, long delayedMs, long periodMs) {
        TaskInfo taskInfo = new TaskInfo(task, delayedMs, periodMs);
        Timber.tag(TAG).d("[%s] schedule period task: %s", mOwnerTag, taskInfo);
        mMainHandler.obtainMessage(MSG_ADD_TASK, taskInfo).sendToTarget();
    }

    public void cancelTask(@NonNull Runnable task) {
        Timber.tag(TAG).d("[%s] cancel task: %s", mOwnerTag, task);
        mMainHandler.obtainMessage(MSG_ADD_TASK, task).sendToTarget();
    }

    public void clear() {
        Timber.tag(TAG).d("[%s] clear tasks", mOwnerTag);
        mMainHandler.sendEmptyMessage(MSG_CLEAR_TASKS);
    }

    public void trigger() {
        Timber.tag(TAG).d("[%s] trigger checking", mOwnerTag);
        mMainHandler.sendEmptyMessage(MSG_CHECK_TASKS);
    }

    @MainThread
    private void removeTask(@NonNull Runnable task) {
        for (int i = 0; i < mTasks.size(); /* empty */) {
            TaskInfo info = mTasks.get(i);
            if (info.task.equals(task)) {
                Timber.tag(TAG).d("[%s] task removed: %s", mOwnerTag, info);
                mTasks.remove(i);
            } else {
                i++;
            }
        }
    }

    @MainThread
    private void checkTasks() {
        Timber.tag(TAG).d("[%s] check tasks, taskCount: %d", mOwnerTag, mTasks.size());
        Iterator<TaskInfo> it = mTasks.iterator();
        ArrayList<Runnable> pendingTasks = new ArrayList<>();
        long nextEventDelay = mCheckInterval;
        while (it.hasNext()) {
            TaskInfo info = it.next();
            if (SystemClock.elapsedRealtime() >= info.triggerAt) {
                Timber.tag(TAG).d("[%s] execute task: %s", mOwnerTag, info);
                pendingTasks.add(info.task);
                if (info.period > 0) {
                    info.triggerAt = SystemClock.elapsedRealtime() + info.period;
                } else {
                    it.remove();
                    info = null; // mark it removed from queue
                }
            }

            if (info != null) {
                long timeout = info.triggerAt - SystemClock.elapsedRealtime();
                if (timeout < nextEventDelay) {
                    nextEventDelay = timeout;
                }
            }
        }

        mMainHandler.removeMessages(MSG_CHECK_TASKS);
        mMainHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, nextEventDelay);

        if (pendingTasks.size() > 0) {
            mTaskExecutor.postTasks(pendingTasks);
        }
    }

    @MainThread
    private void scheduleCheckTask(long delay) {
        if (delay > mCheckInterval) {
            delay = mCheckInterval;
        }
        mMainHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, delay);
    }

    @SuppressLint("HandlerLeak")
    private class MainHandler extends Handler {
        MainHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD_TASK: {
                    TaskInfo task = (TaskInfo) msg.obj;
                    mTasks.add(task);
                    scheduleCheckTask(task.delay);
                    break;
                }

                case MSG_REMOVE_TASK: {
                    Runnable task = (Runnable) msg.obj;
                    removeTask(task);
                    break;
                }

                case MSG_CHECK_TASKS: {
                    checkTasks();
                    break;
                }

                case MSG_CLEAR_TASKS: {
                    Timber.tag(TAG).d("[%s] clear tasks", mOwnerTag);
                    mTasks.clear();
                    mTaskExecutor.clearTasks();
                    break;
                }
            }
        }
    }
}

class TaskInfo {
    private static AtomicInteger sTaskId = new AtomicInteger(1);

    private int taskId;
    Runnable task;
    long delay;
    long period = -1;
    long triggerAt;

    TaskInfo(@NonNull Runnable task, long delay) {
        this.taskId = sTaskId.getAndIncrement();
        this.task = task;
        this.delay = delay;
        this.triggerAt = SystemClock.elapsedRealtime() + delay;
    }

    TaskInfo(@NonNull Runnable task, long delay, long period) {
        this.taskId = sTaskId.getAndIncrement();
        this.task = task;
        this.delay = delay;
        this.period = period;
        this.triggerAt = SystemClock.elapsedRealtime() + delay;
    }

    @Override
    public String toString() {
        long timestamp = System.currentTimeMillis() - (SystemClock.elapsedRealtime() - triggerAt);
        return "TaskInfo[id=" + taskId + ", delay=" + delay
                + ", triggerAt=" + DateTimeUtils.getReadableTimeStamp(timestamp)
                + ", period=" + period + "]";
    }
}
