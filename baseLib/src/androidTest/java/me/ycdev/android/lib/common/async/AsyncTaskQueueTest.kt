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

        val latch = CountDownLatch(2)
        val task1 = Runnable {
            Timber.tag(TAG).d("Executing task1 BEGIN")
            SystemClock.sleep(1000)
            latch.countDown()
            Timber.tag(TAG).d("Executing task1 END")
        }
        val task2 = Runnable {
            Timber.tag(TAG).d("Executing task2 BEGIN")
            // Task1 must be done
            assertThat(latch.count).isEqualTo(1)
            SystemClock.sleep(2000)
            latch.countDown()
            Timber.tag(TAG).d("Executing task2 END")
        }

        // Add tasks
        taskQueue.addTask(task1)
        taskQueue.addTask(task2)

        // Give some time to setup the task handler and check it
        SystemClock.sleep(100)
        val taskHandler = taskQueue.taskHandler
        assertThat(taskHandler).isNotNull()
        val taskThread = taskHandler!!.looper.thread
        val taskTid = taskThread.id
        assertThat(taskThread.name).isEqualTo(TAG)
        assertThat(taskThread.isAlive).isTrue()
        assertThat(ThreadUtils.isThreadRunning(taskTid)).isTrue()
        assertThat(taskTid).isNotEqualTo(Thread.currentThread().id)

        // Waiting for the task done
        assertThat(latch.count).isEqualTo(2)
        latch.await(1000, TimeUnit.MILLISECONDS)
        assertThat(latch.count).isEqualTo(1)
        latch.await(2000, TimeUnit.MILLISECONDS)
        assertThat(latch.count).isEqualTo(0)

        // Waiting for the task thread quit
        assertThat(taskHandler).isNotNull()
        SystemClock.sleep(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN + 100)
        assertThat(taskQueue.taskHandler).isNull()
        assertThat(ThreadUtils.isThreadRunning(taskTid)).isFalse()
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
        val countTask = CountTask(Runnable { latch.countDown() })
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
        val countTask = CountTask(Runnable { latch.countDown() })
        for (i in 0 until count) {
            taskQueue.addTask(countTask, 200)
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
        taskQueue.addTask(countTask, 200)
        taskQueue.removeTask(countTask)

        SystemClock.sleep(500)
        assertThat(countTask.executedCount).isEqualTo(0)
    }

    @Test
    @MediumTest
    fun removeTask_nV1() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        val countTask = CountTask(null)
        val count = 100
        for (i in 0 until count) {
            taskQueue.addTask(countTask, 200)
        }
        taskQueue.removeTask(countTask)

        SystemClock.sleep(500)
        assertThat(countTask.executedCount).isEqualTo(0)
    }

    @Test
    @MediumTest
    @Throws(InterruptedException::class)
    fun removeTask_repeatedWithDelay() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN)

        val count = 100
        val latch = CountDownLatch(count)
        val countTask = CountTask(Runnable { latch.countDown() })
        for (i in 0 until count) {
            taskQueue.removeTask(countTask)
            taskQueue.addTask(countTask, 200)
        }

        // make sure the previous tasks will be executed
        val guardLatch = CountDownLatch(1)
        val guardTask = Runnable {
            guardLatch.countDown()
        }
        taskQueue.addTask(guardTask, 200)
        guardLatch.await()

        assertThat(latch.count).isEqualTo(count - 1)
        assertThat(countTask.executedCount).isEqualTo(1)
    }

    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun setWorkerThreadAutoQuitDelay_normal() {
        val autoQuitDelay = (20 * 1000).toLong()
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(autoQuitDelay)

        val latch = CountDownLatch(1)
        taskQueue.addTask(Runnable { latch.countDown() }, 100)
        latch.await()

        // check if task thread already quited
        assertThat(taskQueue.taskHandler).isNotNull()
        SystemClock.sleep(autoQuitDelay + 100)
        assertThat(taskQueue.taskHandler).isNull()
    }

    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun setWorkerThreadAutoQuitDelay_min() {
        val taskQueue = AsyncTaskQueue(TAG)
        taskQueue.setWorkerThreadAutoQuitDelay(0)

        val latch = CountDownLatch(1)
        taskQueue.addTask(Runnable { latch.countDown() }, 100)
        latch.await()

        // check if task thread already quited
        assertThat(taskQueue.taskHandler).isNotNull()
        SystemClock.sleep(AsyncTaskQueue.WORKER_THREAD_AUTO_QUIT_DELAY_MIN + 100)
        assertThat(taskQueue.taskHandler).isNull()
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

    private class CountTask internal constructor(@param:Nullable private val mTask: Runnable?) :
        Runnable {
        internal var executedCount: Int = 0
            private set

        override fun run() {
            executedCount++
            mTask?.run()
        }
    }

    companion object {
        private const val TAG = "AsyncTaskQueueTest"

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
                val task = Runnable {
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
                taskQueue.addTask(task, taskDelay)
            }
        }
    }
}
