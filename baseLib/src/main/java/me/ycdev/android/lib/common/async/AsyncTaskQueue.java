package me.ycdev.android.lib.common.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import me.ycdev.android.lib.common.utils.Preconditions;
import timber.log.Timber;

/**
 * An utility class for processing tasks async. It's similar to {@link android.app.IntentService}
 * and has following features:
 * <li>1. All tasks are executed one-by-one in a worker thread by {@link Handler}.</li>
 * <li>2. The worker thread is created when needed, and destroyed when not needed anymore.
 *        Also, you can customize the delay time for the thread's auto destroying. </li>
 * <p />
 * Because of the background limits in Android O, we cannot use {@link android.app.IntentService}
 * anymore in background (if the target API is set to Android O or higher versions).
 * This class may be a possible replacement for it.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AsyncTaskQueue {
    private static final String TAG = "AsyncTaskQueue";
    private static final boolean DEV_LOG = false;

    private static final int MSG_MAIN_NEW_TASK = 1;
    private static final int MSG_MAIN_REMOVE_TASK = 2;
    private static final int MSG_MAIN_WORKER_THREAD_QUIT = 3;

    private static final int MSG_WORKER_NEW_TASK = 11;
    private static final int MSG_WORKER_THREAD_QUIT = 12;

    public static final long WORKER_THREAD_AUTO_QUIT_DELAY_MIN = 10 * 1000; // 10 seconds
    public static final long WORKER_THREAD_AUTO_QUIT_DELAY_DEFAULT = 30 * 1000; // 30 seconds

    @NonNull
    private String mName;
    private long mAutoQuitDelay = WORKER_THREAD_AUTO_QUIT_DELAY_DEFAULT;
    private Handler mTaskHandler;

    public AsyncTaskQueue(@NonNull String name) {
        mName = name;
    }

    public void setWorkerThreadAutoQuitDelay(long delay) {
        if (delay < WORKER_THREAD_AUTO_QUIT_DELAY_MIN) {
            Timber.tag(TAG).w("Ignore the requested delay [%d]. Set it to the minimum value [%d].",
                    delay, WORKER_THREAD_AUTO_QUIT_DELAY_MIN);
            mAutoQuitDelay = WORKER_THREAD_AUTO_QUIT_DELAY_MIN;
        } else {
            mAutoQuitDelay = delay;
        }
    }

    public void addTask(Runnable task) {
        addTask(task, 0L);
    }

    public void addTask(Runnable task, long delay) {
        if (DEV_LOG) Timber.tag(TAG).d("addTask: %s, delay: %d", task, delay);
        TaskParams params = new TaskParams(task, delay);
        mMainHandler.obtainMessage(MSG_MAIN_NEW_TASK, params).sendToTarget();
    }

    public void removeTask(Runnable task) {
        if (DEV_LOG) Timber.tag(TAG).d("removeTask: %s", task);
        mMainHandler.obtainMessage(MSG_MAIN_REMOVE_TASK, task).sendToTarget();
    }

    @VisibleForTesting
    Handler getTaskHandler() {
        return mTaskHandler;
    }

    @MainThread
    private void setupTaskHandler() {
        Preconditions.checkMainThread();
        if (mTaskHandler == null) {
            Timber.tag(TAG).d("Creating task thread");
            HandlerThread thread = new HandlerThread(mName);
            thread.start();
            mTaskHandler = new Handler(thread.getLooper(), mTaskCallback);
        }
    }

    @MainThread
    private void prepareForNewTask() {
        mMainHandler.removeMessages(MSG_MAIN_WORKER_THREAD_QUIT);
        setupTaskHandler();
        mTaskHandler.removeMessages(MSG_WORKER_THREAD_QUIT);
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (DEV_LOG) Timber.tag(TAG).d("MainHandler#handleMessage: %s", msg);
            if (msg.what == MSG_MAIN_NEW_TASK) {
                TaskParams params = (TaskParams) msg.obj;
                prepareForNewTask();
                Message taskMessage = mTaskHandler.obtainMessage(MSG_WORKER_NEW_TASK, params.task);
                if (params.delay > 0) {
                    mTaskHandler.sendMessageDelayed(taskMessage, params.delay);
                } else {
                    mTaskHandler.sendMessage(taskMessage);
                }
            } else if (msg.what == MSG_MAIN_REMOVE_TASK) {
                Runnable task = (Runnable) msg.obj;
                prepareForNewTask();
                mTaskHandler.removeMessages(MSG_WORKER_NEW_TASK, task);
            } else if (msg.what == MSG_MAIN_WORKER_THREAD_QUIT) {
                Timber.tag(TAG).d("task thread quiting");
                mTaskHandler.getLooper().quit();
                mTaskHandler = null;
            }
        }
    };

    private Handler.Callback mTaskCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (DEV_LOG) Timber.tag(TAG).d("TaskHandler#handleMessage: %s", msg);
            if (msg.what == MSG_WORKER_NEW_TASK) {
                // Execute the task
                Runnable task = (Runnable) msg.obj;
                task.run();

                // Post a cleaner task!
                // Don't need to check null. If that happens, there MUST be bugs.
                mTaskHandler.removeMessages(MSG_WORKER_THREAD_QUIT);
                if (mAutoQuitDelay > 0) {
                    mTaskHandler.sendEmptyMessageDelayed(MSG_WORKER_THREAD_QUIT, mAutoQuitDelay);
                } else {
                    mTaskHandler.sendEmptyMessage(MSG_WORKER_THREAD_QUIT);
                }
            } else if (msg.what == MSG_WORKER_THREAD_QUIT) {
                mMainHandler.sendEmptyMessage(MSG_MAIN_WORKER_THREAD_QUIT);
            } else {
                return false;
            }

            return true;
        }
    };

    private static class TaskParams {
        Runnable task;
        long delay;

        TaskParams(Runnable task, long delay) {
            this.task = task;
            this.delay = delay;
        }
    }
}
