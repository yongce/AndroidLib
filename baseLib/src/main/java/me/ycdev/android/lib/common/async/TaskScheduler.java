package me.ycdev.android.lib.common.async;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.IntDef;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.VisibleForTesting;
import me.ycdev.android.lib.common.utils.DateTimeUtils;
import me.ycdev.android.lib.common.utils.Preconditions;
import timber.log.Timber;

import static me.ycdev.android.lib.common.async.TaskScheduler.SchedulePolicy.IGNORE;
import static me.ycdev.android.lib.common.async.TaskScheduler.SchedulePolicy.NO_CHECK;
import static me.ycdev.android.lib.common.async.TaskScheduler.SchedulePolicy.REPLACE;
import static me.ycdev.android.lib.common.utils.ThreadUtils.isMainThread;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TaskScheduler {
    private static final String TAG = "TaskScheduler";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_CHECK, IGNORE, REPLACE})
    @interface SchedulePolicy {
        int NO_CHECK = 1;
        int IGNORE = 2;
        int REPLACE = 3;
    }

    private static final int MSG_ADD_TASK = 1;
    private static final int MSG_REMOVE_TASK = 2;
    private static final int MSG_CHECK_TASKS = 3;
    private static final int MSG_CLEAR_TASKS = 4;

    @VisibleForTesting
    static final long DEFAULT_CHECK_INTERVAL = 10_000; // 10 seconds

    private static AtomicInteger sTaskSchedulerId = new AtomicInteger(1);

    private ITaskExecutor mTaskExecutor;
    private String mOwnerTag;
    private long mCheckInterval = DEFAULT_CHECK_INTERVAL;
    private boolean mLogEnabled = false;

    private Handler mMainHandler = new MainHandler();
    private ArrayList<TaskInfo> mTasks = new ArrayList<>();

    // for test only
    @VisibleForTesting int mCheckCount;

    public TaskScheduler(@NonNull ITaskExecutor executor, @NonNull String ownerTag) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(ownerTag);
        mTaskExecutor = executor;
        mOwnerTag = sTaskSchedulerId.getAndIncrement() + "-" + ownerTag;
    }

    public void setCheckInterval(long interval) {
        if (interval < 1000) {
            throw new IllegalArgumentException("Interval less than 1 second is not allowed.");
        }
        mCheckInterval = interval;
    }

    public void enableDebugLogs(boolean enable) {
        mLogEnabled = enable;
    }

    private static String schedulePolicyToString(@SchedulePolicy int policy) {
        switch (policy) {
            case NO_CHECK: return "NO_CHECK";
            case IGNORE: return "IGNORE";
            case REPLACE: return "REPLACE";
            default: throw new RuntimeException("Unknown policy: " + policy);
        }
    }

    private static void checkSchedulePolicy(@SchedulePolicy int policy) {
        switch (policy) {
            case NO_CHECK:
            case IGNORE:
            case REPLACE:
                return;
            default: throw new RuntimeException("Unknown policy: " + policy);
        }
    }

    public void scheduleAt(@NonNull Runnable task, long delayedMs) {
        scheduleAt(task, delayedMs, NO_CHECK);
    }

    public void scheduleAt(@NonNull Runnable task, long delayedMs, @SchedulePolicy int policy) {
        checkSchedulePolicy(policy);
        TaskInfo taskInfo = new TaskInfo(task, delayedMs);
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] schedule one-off task: %s, policy: %s",
                    mOwnerTag, taskInfo, schedulePolicyToString(policy));
        }
        scheduleTask(taskInfo, policy);
    }

    public void schedulePeriod(@NonNull Runnable task, long delayedMs, long periodMs) {
        schedulePeriod(task, delayedMs, periodMs, NO_CHECK);
    }

    public void schedulePeriod(@NonNull Runnable task, long delayedMs, long periodMs,
            @SchedulePolicy int policy) {
        checkSchedulePolicy(policy);
        TaskInfo taskInfo = new TaskInfo(task, delayedMs, periodMs);
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] schedule period task: %s, policy: %s",
                    mOwnerTag, taskInfo, schedulePolicyToString(policy));
        }
        scheduleTask(taskInfo, policy);
    }

    private void scheduleTask(TaskInfo taskInfo, @SchedulePolicy int policy) {
        if (isMainThread()) {
            addTask(taskInfo, policy);
        } else {
            mMainHandler.obtainMessage(MSG_ADD_TASK, policy, 0, taskInfo).sendToTarget();
        }
    }

    public void cancel(@NonNull Runnable task) {
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] cancel task: %s", mOwnerTag, task);
        }
        if (isMainThread()) {
            removeTask(task);
        } else {
            mMainHandler.obtainMessage(MSG_REMOVE_TASK, task).sendToTarget();
        }
    }

    public void clear() {
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] clear tasks", mOwnerTag);
        }
        if (isMainThread()) {
            clearTasks();
        } else {
            mMainHandler.sendEmptyMessage(MSG_CLEAR_TASKS);
        }
    }

    public void trigger() {
        if (mLogEnabled) {
            Timber.tag(TAG).d("[%s] trigger checking", mOwnerTag);
        }
        if (isMainThread()) {
            checkTasks();
        } else {
            mMainHandler.sendEmptyMessage(MSG_CHECK_TASKS);
        }
    }

    @MainThread
    private void addTask(TaskInfo task, @SchedulePolicy int policy) {
        boolean taskAdded = false;
        if (policy == NO_CHECK) {
            mTasks.add(task);
            taskAdded = true;
        } else {
            int index = findTaskIndex(task.task);
            if (index == -1) {
                mTasks.add(task);
                taskAdded = true;
            } else {
                if (mLogEnabled) {
                    Timber.tag(TAG).d("[%s] duplicate task found when add %s", mOwnerTag, task);
                }
                if (policy == REPLACE) {
                    mTasks.set(index, task);
                    taskAdded = true;
                } //else: nothing to do for ignore
            }
        }

        if (taskAdded) {
            scheduleCheckTask(task.delay);
            if (mLogEnabled) {
                Timber.tag(TAG).d("[%s] addTask: %s, policy: %s",
                        mOwnerTag, task, schedulePolicyToString(policy));
            }
        }
    }

    @MainThread
    private int findTaskIndex(@NonNull Runnable task) {
        for (int i = 0; i < mTasks.size(); i++) {
            TaskInfo info = mTasks.get(i);
            if (info.task.equals(task)) {
                return i;
            }
        }
        return -1;
    }

    @MainThread
    private void removeTask(@NonNull Runnable task) {
        for (int i = 0; i < mTasks.size(); /* empty */) {
            TaskInfo info = mTasks.get(i);
            if (info.task.equals(task)) {
                if (mLogEnabled) {
                    Timber.tag(TAG).d("[%s] task removed: %s", mOwnerTag, info);
                }
                mTasks.remove(i);
            } else {
                i++;
            }
        }
    }

    @MainThread
    private void checkTasks() {
        mCheckCount++; // for test only
        if (mTasks.isEmpty()) {
            if (mLogEnabled) {
                Timber.tag(TAG).d("[%s] Tasks empty, cancel check.", mOwnerTag);
            }
            mMainHandler.removeMessages(MSG_CHECK_TASKS);
            return;
        }

        if (mLogEnabled) {
            Timber.tag(TAG).v("[%s] check tasks, taskCount: %d", mOwnerTag, mTasks.size());
        }
        Iterator<TaskInfo> it = mTasks.iterator();
        ArrayList<Runnable> pendingTasks = new ArrayList<>();
        long nextEventDelay = mCheckInterval;
        while (it.hasNext()) {
            TaskInfo info = it.next();
            if (SystemClock.elapsedRealtime() >= info.triggerAt) {
                if (mLogEnabled) {
                    Timber.tag(TAG).d("[%s] task to execute: %s", mOwnerTag, info);
                }
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

        if (mLogEnabled) {
            Timber.tag(TAG).v("[%s] next check at %s", mOwnerTag,
                    DateTimeUtils.getReadableTimeStamp(System.currentTimeMillis() + nextEventDelay));
        }
        mMainHandler.removeMessages(MSG_CHECK_TASKS);
        mMainHandler.sendEmptyMessageDelayed(MSG_CHECK_TASKS, nextEventDelay);

        if (pendingTasks.size() > 0) {
            mTaskExecutor.postTasks(pendingTasks);
        }
    }

    @MainThread
    private void clearTasks() {
        mTasks.clear();
        mTaskExecutor.clearTasks();
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
                    int policy = msg.arg1;
                    addTask(task, policy);
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
                    clearTasks();
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
