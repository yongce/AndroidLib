package me.ycdev.android.lib.common.async;

import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.ycdev.android.lib.common.utils.ThreadUtils;
import timber.log.Timber;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AsyncTaskQueueTest {
    private static final String TAG = "AsyncTaskQueueTest";

    @Test @LargeTest
    public void basic() throws InterruptedException {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        // No task added yet
        assertThat(taskQueue.getTaskHandler(), nullValue());

        CountDownLatch latch = new CountDownLatch(2);
        Runnable task1 = () -> {
            Timber.tag(TAG).d("Executing task1 BEGIN");
            SystemClock.sleep(1000);
            latch.countDown();
            Timber.tag(TAG).d("Executing task1 END");
        };
        Runnable task2 = () -> {
            Timber.tag(TAG).d("Executing task2 BEGIN");
            // Task1 must be done
            assertThat(latch.getCount(), is(1L));
            SystemClock.sleep(2000);
            latch.countDown();
            Timber.tag(TAG).d("Executing task2 END");
        };

        // Add tasks
        taskQueue.addTask(task1);
        taskQueue.addTask(task2);

        // Give some time to setup the task handler and check it
        SystemClock.sleep(100);
        Handler taskHandler = taskQueue.getTaskHandler();
        assertThat(taskHandler, notNullValue());
        Thread taskThread = taskHandler.getLooper().getThread();
        long taskTid = taskThread.getId();
        assertThat(taskThread.getName(), is(TAG));
        assertThat(taskThread.isAlive(), is(true));
        assertThat(ThreadUtils.isThreadRunning(taskTid), is(true));
        assertThat(taskTid, not(Thread.currentThread().getId()));

        // Waiting for the task done
        assertThat(latch.getCount(), is(2L));
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount(), is(1L));
        latch.await(2000, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount(), is(0L));

        // Waiting for the task thread quit
        assertThat(taskHandler, notNullValue());
        SystemClock.sleep(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN + 100);
        assertThat(taskQueue.getTaskHandler(), nullValue());
        assertThat(ThreadUtils.isThreadRunning(taskTid), is(false));
    }

    @Test @LargeTest
    public void addTask_orders() throws InterruptedException {
        addTask_orders(-1);
        addTask_orders(0);
        addTask_orders(100);
        addTask_orders(1000);
    }

    private static void addTask_orders(long delay) throws InterruptedException {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        // No task added yet
        assertThat(taskQueue.getTaskHandler(), nullValue());

        Timber.tag(TAG).d("Add tasks...with delay: %sms", delay);
        final int N = 1000;
        CountDownLatch latch = new CountDownLatch(N);
        final long[] taskTidHolder = new long[]{-1L};
        addTasksAndCheckOrder(taskQueue, N, delay, latch, taskTidHolder);
        latch.await();
        Timber.tag(TAG).d("All task done");
    }

    private static void addTasksAndCheckOrder(AsyncTaskQueue taskQueue, int taskCount,
            long taskDelay, CountDownLatch latch, long[] taskTidHolder) {
        for (int i = 0; i < taskCount; i++) {
            final int taskIndex = i;
            Runnable task = () -> {
                // check tid (should only one task thread created)
                long curTid = Thread.currentThread().getId();
                if (taskTidHolder[0] != -1L) {
                    assertThat(curTid, is(taskTidHolder[0]));
                } else {
                    taskTidHolder[0] = curTid; // init
                }
                // check order
                assertThat(latch.getCount(), is((long)(taskCount - taskIndex)));
                latch.countDown();
            };
            taskQueue.addTask(task, taskDelay);
        }
    }

    @Test @SmallTest
    public void addTask_repeated() throws InterruptedException {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        final int N = 100;
        CountDownLatch latch = new CountDownLatch(N);
        CountTask countTask = new CountTask(latch::countDown);
        for (int i = 0; i < N; i++) {
            taskQueue.addTask(countTask);
        }

        latch.await();
        assertThat(countTask.getExecutedCount(), is(N));
    }

    @Test @MediumTest
    public void addTask_repeatedWithDelay() throws InterruptedException {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        final int N = 100;
        CountDownLatch latch = new CountDownLatch(N);
        CountTask countTask = new CountTask(latch::countDown);
        for (int i = 0; i < N; i++) {
            taskQueue.addTask(countTask, 200);
        }

        latch.await();
        assertThat(countTask.getExecutedCount(), is(N));
    }

    @Test @MediumTest
    public void removeTask_normal() {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        CountTask countTask = new CountTask(null);
        taskQueue.addTask(countTask, 200);
        taskQueue.removeTask(countTask);

        SystemClock.sleep(500);
        assertThat(countTask.getExecutedCount(), is(0));
    }

    @Test @MediumTest
    public void removeTask_nV1() {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        CountTask countTask = new CountTask(null);
        final int N = 100;
        for (int i = 0; i < N; i++) {
            taskQueue.addTask(countTask, 200);
        }
        taskQueue.removeTask(countTask);

        SystemClock.sleep(500);
        assertThat(countTask.getExecutedCount(), is(0));
    }

    @Test @MediumTest
    public void removeTask_repeatedWithDelay() throws InterruptedException {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN);

        final int N = 100;
        CountDownLatch latch = new CountDownLatch(N);
        CountTask countTask = new CountTask(latch::countDown);
        for (int i = 0; i < N; i++) {
            taskQueue.removeTask(countTask);
            taskQueue.addTask(countTask, 200);
        }

        latch.await(500, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount(), is((long)(N - 1)));
        assertThat(countTask.getExecutedCount(), is(1));
    }

    @Test @LargeTest
    public void setWorkerThreadAutoQuitDelay_normal() throws InterruptedException {
        final long AUTO_QUIT_DELAY = 20 * 1000;
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(AUTO_QUIT_DELAY);

        CountDownLatch latch = new CountDownLatch(1);
        taskQueue.addTask(latch::countDown, 100);
        latch.await();

        // check if task thread already quited
        assertThat(taskQueue.getTaskHandler(), notNullValue());
        SystemClock.sleep(AUTO_QUIT_DELAY + 100);
        assertThat(taskQueue.getTaskHandler(), nullValue());
    }

    @Test @LargeTest
    public void setWorkerThreadAutoQuitDelay_min() throws InterruptedException {
        AsyncTaskQueue taskQueue = new AsyncTaskQueue(TAG);
        taskQueue.setWorkerThreadAutoQuitDelay(0);

        CountDownLatch latch = new CountDownLatch(1);
        taskQueue.addTask(latch::countDown, 100);
        latch.await();

        // check if task thread already quited
        assertThat(taskQueue.getTaskHandler(), notNullValue());
        SystemClock.sleep(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN + 100);
        assertThat(taskQueue.getTaskHandler(), nullValue());
    }

    @Test @SmallTest
    public void multipleTaskQueues() throws InterruptedException {
        AsyncTaskQueue taskQueue1 = new AsyncTaskQueue(TAG);
        taskQueue1.setWorkerThreadAutoQuitDelay(0);

        AsyncTaskQueue taskQueue2 = new AsyncTaskQueue(TAG);
        taskQueue2.setWorkerThreadAutoQuitDelay(0);

        final int N1 = 100;
        final int N2 = 200;
        final CountDownLatch latch1 = new CountDownLatch(N1);
        final CountDownLatch latch2 = new CountDownLatch(N2);
        final long[] taskTidHolder1 = new long[]{-1L};
        final long[] taskTidHolder2 = new long[]{-1L};

        addTasksAndCheckOrder(taskQueue1, N1, 50, latch1, taskTidHolder1);
        addTasksAndCheckOrder(taskQueue2, N2, 50, latch2, taskTidHolder2);

        latch1.await();
        latch2.await();

        assertThat(taskTidHolder1[0], not(-1L));
        assertThat(taskTidHolder1[0], not(Thread.currentThread().getId()));
        assertThat(taskTidHolder2[0], not(-1L));
        assertThat(taskTidHolder2[0], not(Thread.currentThread().getId()));

        assertThat(taskTidHolder1[0], not(taskTidHolder2[0]));
    }

    private static class CountTask implements Runnable {
        private Runnable mTask;
        private int mExecutedCount;

        CountTask(@Nullable Runnable task) {
            mTask = task;
        }

        int getExecutedCount() {
            return mExecutedCount;
        }

        @Override
        public void run() {
            mExecutedCount++;
            if (mTask != null) {
                mTask.run();
            }
        }
    }
}
