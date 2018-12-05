package me.ycdev.android.lib.common.async

import android.os.SystemClock
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@LargeTest
class TaskSchedulerTest {
    private fun createScheduler(mainThread : Boolean) : TaskScheduler {
        val taskExecutor = if (mainThread) HandlerExecutor.withMainLooper() else HandlerThreadExecutor("test")
        val taskScheduler = TaskScheduler(taskExecutor, "test")
        taskScheduler.enableDebugLogs(mainThread)
        return taskScheduler
    }

    @Test @MediumTest
    fun scheduleAt_basic() {
        scheduleAt_basic(true)
        scheduleAt_basic(false)
    }

    private fun scheduleAt_basic(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(1)
        val startTime = SystemClock.elapsedRealtime()
        taskScheduler.scheduleAt({
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }, 500)
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0)
    }

    @Test @MediumTest
    fun scheduleAt_policy_noCheck() {
        scheduleAt_policy_noCheck(true)
        scheduleAt_policy_noCheck(false)
    }

    private fun scheduleAt_policy_noCheck(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        taskScheduler.scheduleAt(task, 500)
        taskScheduler.scheduleAt(task, 500, TaskScheduler.SchedulePolicy.NO_CHECK)
        taskScheduler.scheduleAt(task, 500)
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0)
    }

    @Test @LargeTest
    fun scheduleAt_policy_ignore() {
        scheduleAt_policy_ignore(true)
        scheduleAt_policy_ignore(false)
    }

    private fun scheduleAt_policy_ignore(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        taskScheduler.scheduleAt(SameTaskWrapper(Runnable { latch.countDown() }, 101),
                500, TaskScheduler.SchedulePolicy.IGNORE)
        taskScheduler.scheduleAt(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, TaskScheduler.SchedulePolicy.IGNORE)
        taskScheduler.scheduleAt(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, TaskScheduler.SchedulePolicy.IGNORE)
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
    }

    @Test @LargeTest
    fun scheduleAt_policy_replace() {
        scheduleAt_policy_replace(true)
        scheduleAt_policy_replace(false)
    }

    private fun scheduleAt_policy_replace(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        taskScheduler.scheduleAt(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, TaskScheduler.SchedulePolicy.REPLACE)
        taskScheduler.scheduleAt(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, TaskScheduler.SchedulePolicy.REPLACE)
        taskScheduler.scheduleAt(SameTaskWrapper(Runnable { latch.countDown() }, 101),
                500, TaskScheduler.SchedulePolicy.REPLACE)
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
    }

    @Test @LargeTest
    fun schedulePeriod_basic() {
        schedulePeriod_basic(true)
        schedulePeriod_basic(false)
    }

    private fun schedulePeriod_basic(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        // at +500ms, +1100ms, + 1700ms
        taskScheduler.schedulePeriod(task, 500, 600)
        latch.await(2, TimeUnit.SECONDS)
        assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(1700)
        assertThat(latch.count).isEqualTo(0)
        taskScheduler.cancel(task)
    }

    @Test @MediumTest
    fun schedulePeriod_noCheck() {
        schedulePeriod_noCheck(true)
        schedulePeriod_noCheck(false)
    }

    private fun schedulePeriod_noCheck(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }
        taskScheduler.schedulePeriod(task, 500, 1000)
        taskScheduler.schedulePeriod(task, 500, 1000, TaskScheduler.SchedulePolicy.NO_CHECK)
        taskScheduler.schedulePeriod(task, 500, 1000)
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0)
        taskScheduler.cancel(task)
    }

    @Test @LargeTest
    fun schedulePeriod_policy_ignore() {
        schedulePeriod_policy_ignore(true)
        schedulePeriod_policy_ignore(false)
    }

    private fun schedulePeriod_policy_ignore(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = SameTaskWrapper(Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }, 101)
        taskScheduler.schedulePeriod(task, 500, 1000, TaskScheduler.SchedulePolicy.IGNORE)
        taskScheduler.schedulePeriod(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, 1000, TaskScheduler.SchedulePolicy.IGNORE)
        taskScheduler.schedulePeriod(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, 1000, TaskScheduler.SchedulePolicy.IGNORE)
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
        taskScheduler.cancel(task)
    }

    @Test @LargeTest
    fun schedulePeriod_policy_replace() {
        schedulePeriod_policy_replace(true)
        schedulePeriod_policy_replace(false)
    }

    private fun schedulePeriod_policy_replace(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(3)
        val startTime = SystemClock.elapsedRealtime()
        val task = SameTaskWrapper(Runnable {
            assertThat(SystemClock.elapsedRealtime() - startTime).isAtLeast(500)
            latch.countDown()
        }, 101)
        taskScheduler.schedulePeriod(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, 1000, TaskScheduler.SchedulePolicy.REPLACE)
        taskScheduler.schedulePeriod(SameTaskWrapper(Runnable { fail("Should be ignored") }, 101),
                500, 1000, TaskScheduler.SchedulePolicy.REPLACE)
        taskScheduler.schedulePeriod(task, 500, 1000, TaskScheduler.SchedulePolicy.REPLACE)
        latch.await(1, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(2)
        taskScheduler.cancel(task)
    }

    @Test @LargeTest
    fun setCheckInterval_once() {
        setCheckInterval_once(true)
        setCheckInterval_once(false)
    }

    private fun setCheckInterval_once(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        taskScheduler.setCheckInterval(1000) // 1 second

        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.scheduleAt(task, 2500)
        latch.await(3, TimeUnit.SECONDS)

        assertThat(latch.count).isEqualTo(0)
        // check at 1000, 2000, 2500
        assertThat(taskScheduler.mCheckCount).isEqualTo(3)
    }

    @Test @LargeTest
    fun setCheckInterval_period() {
        setCheckInterval_period(true)
        setCheckInterval_period(false)
    }

    private fun setCheckInterval_period(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        taskScheduler.setCheckInterval(1000) // 1 second

        val latch = CountDownLatch(2)
        val task = Runnable {
            latch.countDown()
        }
        // at 500, 2700
        taskScheduler.schedulePeriod(task, 500, 2200)
        latch.await(3, TimeUnit.SECONDS)

        assertThat(latch.count).isEqualTo(0)
        // check at 500, 1500, 2500, 2700
        assertThat(taskScheduler.mCheckCount).isEqualTo(4)
        taskScheduler.cancel(task)
    }

    @Test
    fun setCheckInterval_default() {
        val taskScheduler = createScheduler(true)
        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.scheduleAt(task, TaskScheduler.DEFAULT_CHECK_INTERVAL + 2000)

        latch.await(TaskScheduler.DEFAULT_CHECK_INTERVAL - 1000, TimeUnit.MILLISECONDS)
        assertThat(latch.count).isEqualTo(1)
        assertThat(taskScheduler.mCheckCount).isEqualTo(0)

        latch.await(2000, TimeUnit.MILLISECONDS)
        assertThat(latch.count).isEqualTo(1)
        assertThat(taskScheduler.mCheckCount).isEqualTo(1)
    }

    @Test
    fun trigger() {
        trigger(true)
        trigger(false)
    }

    private fun trigger(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.scheduleAt(task, 2000)

        SystemClock.sleep(500)
        taskScheduler.trigger()
        SystemClock.sleep(500)
        taskScheduler.trigger()
        SystemClock.sleep(500)

        assertThat(taskScheduler.mCheckCount).isEqualTo(2)
        assertThat(latch.count).isEqualTo(1)
        taskScheduler.cancel(task)
    }

    @Test
    fun cancel_once() {
        cancel_once(true)
        cancel_once(false)
    }

    private fun cancel_once(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(1)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.scheduleAt(task, 1500)
        SystemClock.sleep(1000)
        taskScheduler.cancel(task)
        latch.await(2, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(1)
    }

    @Test
    fun cancel_period() {
        cancel_period(true)
        cancel_period(false)
    }

    private fun cancel_period(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(2)
        val task = Runnable {
            latch.countDown()
        }
        taskScheduler.schedulePeriod(task, 500, 1000)
        SystemClock.sleep(1000)
        taskScheduler.cancel(task)
        latch.await(2, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(1)
    }

    @Test
    fun clear() {
        clear(true)
        clear(false)
    }

    private fun clear(mainThread: Boolean) {
        val taskScheduler = createScheduler(mainThread)
        val latch = CountDownLatch(2)
        taskScheduler.scheduleAt({ latch.countDown() }, 1500)
        taskScheduler.schedulePeriod({ latch.countDown() }, 500, 1000)
        SystemClock.sleep(1000)
        taskScheduler.clear()
        latch.await(2, TimeUnit.SECONDS) // will timeout
        assertThat(latch.count).isEqualTo(1)
    }

}

class SameTaskWrapper(target: Runnable, id : Int) : Runnable {
    private val mTarget : Runnable = target
    private val mId : Int = id

    override fun run() {
        mTarget.run()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return  true
        }

        if (other !is SameTaskWrapper) {
            return false
        }

        return mId == other.mId
    }

    override fun hashCode(): Int {
        var result = mTarget.hashCode()
        result = 31 * result + mId
        return result
    }
}