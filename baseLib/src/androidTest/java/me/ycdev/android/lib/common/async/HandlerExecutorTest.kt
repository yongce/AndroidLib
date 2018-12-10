package me.ycdev.android.lib.common.async

import android.os.Looper
import android.os.SystemClock
import androidx.test.filters.LargeTest

import org.junit.Test

import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import com.google.common.truth.Truth.assertThat

@LargeTest
class HandlerExecutorTest {
    @Test
    @Throws(InterruptedException::class)
    fun checkLooper() {
        println("test thread id=" + Thread.currentThread().id)
        val executor = HandlerExecutor(Looper.getMainLooper())
        val latch = CountDownLatch(4)
        executor.postTasks(createTasks(latch, 4, 150))
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0L)
    }

    @Test
    @Throws(InterruptedException::class)
    fun clearTasks() {
        val executor = HandlerExecutor(Looper.getMainLooper())
        val latch = CountDownLatch(5)
        executor.postTasks(createTasks(latch, 5, 100))
        SystemClock.sleep(250)
        executor.clearTasks()
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isGreaterThan(1L)
        assertThat(latch.count).isLessThan(5L)
    }

    private fun createTasks(latch: CountDownLatch, count: Int, sleepMs: Long): List<Runnable> {
        val tasks = ArrayList<Runnable>(count)
        for (i in 0 until count) {
            tasks.add(Runnable {
                println("main thread id=" + Thread.currentThread().id)
                assertThat(Looper.myLooper()!!).isSameAs(Looper.getMainLooper())
                SystemClock.sleep(sleepMs)
                latch.countDown()
            })
        }
        return tasks
    }
}
