package me.ycdev.android.lib.common.async

import android.os.SystemClock
import androidx.annotation.Nullable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import me.ycdev.android.lib.common.utils.MainHandler
import me.ycdev.android.lib.common.utils.ThreadUtils
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
@LargeTest
class AsyncTaskQueueTest {
    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun basic() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        // No task added yet
        assertThat(taskQueue.taskHandler).isNull()

        val firstTaskDone = CountDownLatch(1)
        val allTasksDone = CountDownLatch(2)
        val executionOrder = AtomicInteger(0)
        val task1 =
            Runnable {
                Timber.tag(TAG).d("Executing task1 BEGIN")
                assertThat(executionOrder.incrementAndGet()).isEqualTo(1)
                SystemClock.sleep(1000)
                firstTaskDone.countDown()
                allTasksDone.countDown()
                Timber.tag(TAG).d("Executing task1 END")
            }
        val task2 =
            Runnable {
                Timber.tag(TAG).d("Executing task2 BEGIN")
                // Task1 must be done
                assertThat(executionOrder.incrementAndGet()).isEqualTo(2)
                assertThat(firstTaskDone.count).isEqualTo(0)
                SystemClock.sleep(2000)
                allTasksDone.countDown()
                Timber.tag(TAG).d("Executing task2 END")
            }

        // Add tasks
        taskQueue.addTask(task1)
        taskQueue.addTask(task2)

        val taskHandler = waitForTaskHandlerCreated(taskQueue)
        assertThat(taskHandler).isNotNull()
        val taskThread = taskHandler!!.looper.thread
        val taskTid = taskThread.id
        assertThat(taskThread.name).isEqualTo(TAG)
        assertThat(taskThread.isAlive).isTrue()
        assertThat(ThreadUtils.isThreadRunning(taskTid)).isTrue()
        assertThat(taskTid).isNotEqualTo(Thread.currentThread().id)

        // Waiting for the task done
        assertThat(firstTaskDone.await(10, TimeUnit.SECONDS)).isTrue()
        assertThat(allTasksDone.await(10, TimeUnit.SECONDS)).isTrue()
        assertThat(allTasksDone.count).isEqualTo(0)

        // Waiting for the task thread quit
        assertThat(taskHandler).isNotNull()
        assertThat(waitForTaskThreadStopped(taskQueue, taskTid)).isTrue()
    }

    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun addTask_orders() {
        addTask_orders(-1)
        addTask_orders(0)
        addTask_orders(100)
        addTask_orders(1000)
    }

    @Test
    @SmallTest
    @Throws(InterruptedException::class)
    fun addTask_repeated() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        val count = 100
        val latch = CountDownLatch(count)
        val countTask = CountTask { latch.countDown() }
        for (i in 0 until count) {
            taskQueue.addTask(countTask)
        }

        latch.await()
        assertThat(countTask.executedCount).isEqualTo(count)
    }

    @Test
    @MediumTest
    @Throws(InterruptedException::class)
    fun addTask_repeatedWithDelay() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        val count = 100
        val latch = CountDownLatch(count)
        val countTask = CountTask { latch.countDown() }
        for (i in 0 until count) {
            taskQueue.addTask(200, countTask)
        }

        latch.await()
        assertThat(countTask.executedCount).isEqualTo(count)
    }

    @Test
    @MediumTest
    fun removeTask_normal() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        val countTask = CountTask(null)
        taskQueue.addTask(200, countTask)
        taskQueue.removeTask(countTask)

        SystemClock.sleep(500)
        assertThat(countTask.executedCount).isEqualTo(0)
    }

    @Test
    @MediumTest
    fun removeTask_nV1() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        // make sure the task handler will be blocked
        val guardLatch = CountDownLatch(1)
        val guardTask = Runnable { guardLatch.await() }
        taskQueue.addTask(guardTask)

        // add many tasks and then remove them
        val countTask = CountTask(null)
        val count = 100
        for (i in 0 until count) {
            taskQueue.addTask(100, countTask)
        }
        taskQueue.removeTask(countTask)

        // make sure all messages in main looper had been processed
        MainHandler.post {
            // now, let the guard task go
            guardLatch.countDown()
        }

        // add one more task
        taskQueue.addTask(100, countTask)

        // make sure the previous tasks will be executed
        waitForTasksDone(taskQueue, 100)

        assertThat(countTask.executedCount).isEqualTo(1)
    }

    @Test
    @MediumTest
    @Throws(InterruptedException::class)
    fun removeTask_repeatedWithDelay() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        val count = 100
        val latch = CountDownLatch(count)
        val countTask = CountTask { latch.countDown() }
        for (i in 0 until count) {
            taskQueue.removeTask(countTask)
            taskQueue.addTask(200, countTask)
        }

        // make sure the previous tasks will be executed
        waitForTasksDone(taskQueue, 200)

        assertThat(latch.count).isEqualTo(count - 1)
        assertThat(countTask.executedCount).isEqualTo(1)
    }

    private fun waitForTasksDone(
        taskQueue: AsyncTaskQueue,
        delay: Long
    ) {
        val guardLatch = CountDownLatch(1)
        val guardTask =
            Runnable {
                guardLatch.countDown()
            }
        taskQueue.addTask(delay, guardTask)
        guardLatch.await()
    }

    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun setWorkerThreadAutoQuitDelay_normal() {
        val autoQuitDelay = (10 * 1000).toLong()
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(autoQuitDelay)

        val latch = CountDownLatch(1)
        taskQueue.addTask(100) { latch.countDown() }
        latch.await()

        // check if task thread already quited
        assertThat(taskQueue.taskHandler).isNotNull()
        assertThat(waitForTaskHandlerStopped(taskQueue)).isTrue()
    }

    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun setWorkerThreadAutoQuitDelay_min() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(0)

        val latch = CountDownLatch(1)
        taskQueue.addTask(100) { latch.countDown() }
        latch.await()

        // check if task thread already quited
        val taskHandler = taskQueue.taskHandler
        assertThat(taskHandler).isNotNull()
        assertThat(waitForTaskThreadStopped(taskQueue, taskHandler!!.looper.thread.id)).isTrue()
    }

    @Test
    @SmallTest
    @Throws(InterruptedException::class)
    fun multipleTaskQueues() {
        val taskQueue1 = AsyncTaskQueue(TAG)
        taskQueue1.setWorkerThreadAutoQuitDelay(0)

        val taskQueue2 = AsyncTaskQueue(TAG)
        taskQueue2.setWorkerThreadAutoQuitDelay(0)

        val count1 = 100
        val count2 = 200
        val latch1 = CountDownLatch(count1)
        val latch2 = CountDownLatch(count2)
        val taskTidHolder1 = longArrayOf(-1L)
        val taskTidHolder2 = longArrayOf(-1L)

        addTasksAndCheckOrder(taskQueue1, count1, 50, latch1, taskTidHolder1)
        addTasksAndCheckOrder(taskQueue2, count2, 50, latch2, taskTidHolder2)

        latch1.await()
        latch2.await()

        assertThat(taskTidHolder1[0]).isNotEqualTo(-1L)
        assertThat(taskTidHolder1[0]).isNotEqualTo(Thread.currentThread().id)
        assertThat(taskTidHolder2[0]).isNotEqualTo(-1L)
        assertThat(taskTidHolder2[0]).isNotEqualTo(Thread.currentThread().id)

        assertThat(taskTidHolder1[0]).isNotEqualTo(taskTidHolder2[0])
    }

    private class CountTask(
        @param:Nullable private val mTask: Runnable?
    ) : Runnable {
        var executedCount: Int = 0
            private set

        override fun run() {
            executedCount++
            mTask?.run()
        }
    }

    companion object {
        private const val TAG = "AsyncTaskQueueTest"
        private const val WAIT_INTERVAL_MS = 100L
        private const val TASK_THREAD_STOP_TIMEOUT_MS = 15_000L

        private fun waitForTaskHandlerCreated(taskQueue: AsyncTaskQueue): android.os.Handler? {
            val deadline = SystemClock.elapsedRealtime() + 10_000L
            while (SystemClock.elapsedRealtime() < deadline) {
                taskQueue.taskHandler?.let { return it }
                SystemClock.sleep(WAIT_INTERVAL_MS)
            }
            return taskQueue.taskHandler
        }

        @Throws(InterruptedException::class)
        private fun addTask_orders(delay: Long) {
            val taskQueue = AsyncTaskQueue(TAG)
            taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

            // No task added yet
            assertThat(taskQueue.taskHandler).isNull()

            Timber.tag(TAG).d("Add tasks...with delay: %sms", delay)
            val count = 1000
            val latch = CountDownLatch(count)
            val taskTidHolder = longArrayOf(-1L)
            addTasksAndCheckOrder(taskQueue, count, delay, latch, taskTidHolder)
            latch.await()
            Timber.tag(TAG).d("All task done")
        }

        private fun addTasksAndCheckOrder(
            taskQueue: AsyncTaskQueue,
            taskCount: Int,
            taskDelay: Long,
            latch: CountDownLatch,
            taskTidHolder: LongArray
        ) {
            for (i in 0 until taskCount) {
                val task =
                    Runnable {
                        // check tid (should only one task thread created)
                        val curTid = Thread.currentThread().id
                        if (taskTidHolder[0] != -1L) {
                            assertThat(curTid).isEqualTo(taskTidHolder[0])
                        } else {
                            taskTidHolder[0] = curTid // init
                        }
                        // check order
                        assertThat(latch.count).isEqualTo(taskCount - i)
                        latch.countDown()
                    }
                taskQueue.addTask(taskDelay, task)
            }
        }

        private fun waitForTaskHandlerStopped(taskQueue: AsyncTaskQueue): Boolean {
            val deadline = SystemClock.elapsedRealtime() + TASK_THREAD_STOP_TIMEOUT_MS
            while (SystemClock.elapsedRealtime() < deadline) {
                if (taskQueue.taskHandler == null) {
                    return true
                }
                SystemClock.sleep(WAIT_INTERVAL_MS)
            }
            return taskQueue.taskHandler == null
        }

        private fun waitForTaskThreadStopped(
            taskQueue: AsyncTaskQueue,
            taskTid: Long
        ): Boolean {
            val deadline = SystemClock.elapsedRealtime() + TASK_THREAD_STOP_TIMEOUT_MS
            while (SystemClock.elapsedRealtime() < deadline) {
                if (taskQueue.taskHandler == null && !ThreadUtils.isThreadRunning(taskTid)) {
                    return true
                }
                SystemClock.sleep(WAIT_INTERVAL_MS)
            }
            return taskQueue.taskHandler == null && !ThreadUtils.isThreadRunning(taskTid)
        }
    }
}
