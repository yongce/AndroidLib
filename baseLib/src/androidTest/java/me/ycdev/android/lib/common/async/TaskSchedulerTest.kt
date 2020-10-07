package me.ycdev.android.lib.common.async

import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import org.junit.After
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@LargeTest
class TaskSchedulerTest {
    private lateinit var schedulerLooper: Looper
    private lateinit var handlerThreadExecutor: HandlerTaskExecutor

    @Before
    fun setup() {
        val thread = HandlerThread("TaskScheduler")
        thread.start()
        schedulerLooper = thread.looper
        handlerThreadExecutor = HandlerTaskExecutor.withHandlerThread("TaskExecutor")
    }

    @After
    fun tearDown() {
        schedulerLooper.quit()
        handlerThreadExecutor.taskHandler.looper.quit()
    }

    private fun createScheduler(mainThread: Boolean): TaskScheduler {
        val looper = if (mainThread) Looper.getMainLooper() else schedulerLooper
        val taskScheduler = TaskScheduler(looper, "test")
        taskScheduler.enableDebugLogs(mainThread)
        return taskScheduler
    }

    @Test @MediumTest
    fun scheduleAt_basic() {
        scheduleAt_basic(true, HandlerTaskExecutor.withMainLooper())
        scheduleAt_basic(true, handlerThreadExecutor)
        scheduleAt_basic(false, HandlerTaskExecutor.withMainLooper())
        scheduleAt_basic(false, handlerThreadExecutor)
    }

    private fun scheduleAt_basic(mainThreadScheduler: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThreadScheduler)
        val latch = CountDownLatch(1)
        val startTime = SystemClock.elapsedRealtime()
        taskScheduler.schedule(executor, 500) {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0)
    }

    @Test @MediumTest
    fun scheduleAt_policy_noCheck() {
        scheduleAt_policy_noCheck(true, HandlerTaskExecutor.withMainLooper())
        scheduleAt_policy_noCheck(true, handlerThreadExecutor)
        scheduleAt_policy_noCheck(false, HandlerTaskExecutor.withMainLooper())
        scheduleAt_policy_noCheck(false, handlerThreadExecutor)
    }

    private fun scheduleAt_policy_noCheck(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        taskScheduler.schedule(executor, 500, task)
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_NO_CHECK, task)
        taskScheduler.schedule(executor, 500, task)
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0)
    }

    @Test @LargeTest
    fun scheduleAt_policy_ignore() {
        scheduleAt_policy_ignore(true, HandlerTaskExecutor.withMainLooper())
        scheduleAt_policy_ignore(true, handlerThreadExecutor)
        scheduleAt_policy_ignore(false, HandlerTaskExecutor.withMainLooper())
        scheduleAt_policy_ignore(false, handlerThreadExecutor)
    }

    private fun scheduleAt_policy_ignore(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_IGNORE,
            SameTaskWrapper({ latch.countDown() }, 101))
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_IGNORE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_IGNORE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
    }

    @Test @LargeTest
    fun scheduleAt_policy_replace() {
        scheduleAt_policy_replace(true, HandlerTaskExecutor.withMainLooper())
        scheduleAt_policy_replace(true, handlerThreadExecutor)
        scheduleAt_policy_replace(false, HandlerTaskExecutor.withMainLooper())
        scheduleAt_policy_replace(false, handlerThreadExecutor)
    }

    private fun scheduleAt_policy_replace(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_REPLACE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_REPLACE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        taskScheduler.schedule(executor, 500, TaskScheduler.SCHEDULE_POLICY_REPLACE,
            SameTaskWrapper({ latch.countDown() }, 101))
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
    }

    @Test @LargeTest
    fun schedulePeriod_basic() {
        schedulePeriod_basic(true, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_basic(true, handlerThreadExecutor)
        schedulePeriod_basic(false, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_basic(false, handlerThreadExecutor)
    }

    private fun schedulePeriod_basic(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        // at +500ms, +1100ms, + 1700ms
        taskScheduler.schedulePeriod(executor, 500, 600, task)
        latch.await(2, TimeUnit.SECONDS)
        assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(1700)
        assertThat(latch.count).isEqualTo(0)
        taskScheduler.cancel(task)
    }

    @Test @MediumTest
    fun schedulePeriod_noCheck() {
        schedulePeriod_noCheck(true, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_noCheck(true, handlerThreadExecutor)
        schedulePeriod_noCheck(false, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_noCheck(false, handlerThreadExecutor)
    }

    private fun schedulePeriod_noCheck(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        taskScheduler.schedulePeriod(executor, 500, 1000, task)
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_NO_CHECK, task)
        taskScheduler.schedulePeriod(executor, 500, 1000, task)
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0)
        taskScheduler.cancel(task)
    }

    @Test @LargeTest
    fun schedulePeriod_policy_ignore() {
        schedulePeriod_policy_ignore(true, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_policy_ignore(true, handlerThreadExecutor)
        schedulePeriod_policy_ignore(false, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_policy_ignore(false, handlerThreadExecutor)
    }

    private fun schedulePeriod_policy_ignore(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = SameTaskWrapper({
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }, 101)
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_IGNORE, task)
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_IGNORE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_IGNORE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
        taskScheduler.cancel(task)
    }

    @Test @LargeTest
    fun schedulePeriod_policy_replace() {
        schedulePeriod_policy_replace(true, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_policy_replace(true, handlerThreadExecutor)
        schedulePeriod_policy_replace(false, HandlerTaskExecutor.withMainLooper())
        schedulePeriod_policy_replace(false, handlerThreadExecutor)
    }

    private fun schedulePeriod_policy_replace(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = SameTaskWrapper({
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }, 101)
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_REPLACE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_REPLACE,
            SameTaskWrapper({ fail("Should be ignored") }, 101))
        taskScheduler.schedulePeriod(executor, 500, 1000, TaskScheduler.SCHEDULE_POLICY_REPLACE, task)
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
        taskScheduler.cancel(task)
    }

    @Test @LargeTest
    fun setCheckInterval_once() {
        setCheckInterval_once(true, HandlerTaskExecutor.withMainLooper())
        setCheckInterval_once(true, handlerThreadExecutor)
        setCheckInterval_once(false, HandlerTaskExecutor.withMainLooper())
        setCheckInterval_once(false, handlerThreadExecutor)
    }

    private fun setCheckInterval_once(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        taskScheduler.setCheckInterval(1000) // 1 second

        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.schedule(executor, 2500, task)
        latch.await(3, TimeUnit.SECONDS)

        assertThat(latch.count).isEqualTo(0)
        // check at 1000, 2000, 2500
        assertThat(taskScheduler.checkCount).isEqualTo(3)
    }

    @Test @LargeTest
    fun setCheckInterval_period() {
        setCheckInterval_period(true, HandlerTaskExecutor.withMainLooper())
        setCheckInterval_period(true, handlerThreadExecutor)
        setCheckInterval_period(false, HandlerTaskExecutor.withMainLooper())
        setCheckInterval_period(false, handlerThreadExecutor)
    }

    private fun setCheckInterval_period(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        taskScheduler.setCheckInterval(1000) // 1 second

        val latch = CountDownLatch(2)
        val task = Runnable {
            latch.countDown()
        }
        // at 500, 2700
        taskScheduler.schedulePeriod(executor, 500, 2200, task)
        latch.await(3, TimeUnit.SECONDS)

        assertThat(latch.count).isEqualTo(0)
        // check at 500, 1500, 2500, 2700
        assertThat(taskScheduler.checkCount).isEqualTo(4)
        taskScheduler.cancel(task)
    }

    @Test
    fun setCheckInterval_default() {
        val taskScheduler = createScheduler(true)
        val executor = HandlerTaskExecutor.withMainLooper()
        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.schedule(executor, TaskScheduler.DEFAULT_CHECK_INTERVAL + 2000, task)

        latch.await(TaskScheduler.DEFAULT_CHECK_INTERVAL - 1000, TimeUnit.MILLISECONDS)
        assertThat(latch.count).isEqualTo(1)
        assertThat(taskScheduler.checkCount).isEqualTo(0)

        latch.await(2000, TimeUnit.MILLISECONDS)
        assertThat(latch.count).isEqualTo(1)
        assertThat(taskScheduler.checkCount).isEqualTo(1)
    }

    @Test
    fun trigger() {
        trigger(true, HandlerTaskExecutor.withMainLooper())
        trigger(true, handlerThreadExecutor)
        trigger(false, HandlerTaskExecutor.withMainLooper())
        trigger(false, handlerThreadExecutor)
    }

    private fun trigger(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.schedule(executor, 2000, task)

        SystemClock.sleep(500)
        taskScheduler.trigger()
        SystemClock.sleep(500)
        taskScheduler.trigger()
        SystemClock.sleep(500)

        assertThat(taskScheduler.checkCount).isEqualTo(2)
        assertThat(latch.count).isEqualTo(1)
        taskScheduler.cancel(task)
    }

    @Test
    fun cancel_once() {
        cancel_once(true, HandlerTaskExecutor.withMainLooper())
        cancel_once(true, handlerThreadExecutor)
        cancel_once(false, HandlerTaskExecutor.withMainLooper())
        cancel_once(false, handlerThreadExecutor)
    }

    private fun cancel_once(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.schedule(executor, 1500, task)
        SystemClock.sleep(1000)
        taskScheduler.cancel(task)
        latch.await(2, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(1)
    }

    @Test
    fun cancel_period() {
        cancel_period(true, HandlerTaskExecutor.withMainLooper())
        cancel_period(true, handlerThreadExecutor)
        cancel_period(false, HandlerTaskExecutor.withMainLooper())
        cancel_period(false, handlerThreadExecutor)
    }

    private fun cancel_period(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(2)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.schedulePeriod(executor, 500, 1000, task)
        SystemClock.sleep(1000)
        taskScheduler.cancel(task)
        latch.await(2, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(1)
    }

    @Test
    fun clear() {
        clear(true, HandlerTaskExecutor.withMainLooper())
        clear(true, handlerThreadExecutor)
        clear(false, HandlerTaskExecutor.withMainLooper())
        clear(false, handlerThreadExecutor)
    }

    private fun clear(mainThread: Boolean, executor: ITaskExecutor) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(2)
        taskScheduler.schedule(executor, 1500) { latch.countDown() }
        taskScheduler.schedulePeriod(executor, 500, 1000) { latch.countDown() }
        SystemClock.sleep(1000)
        taskScheduler.clear()
        latch.await(2, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(1)
    }
}

class SameTaskWrapper(private val target: Runnable, private val id: Int) : Runnable {
    override fun run() {
        target.run()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is SameTaskWrapper) {
            return false
        }

        return id == other.id
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + id
        return result
    }
}
