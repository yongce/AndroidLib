package me.ycdev.android.lib.common.async

import android.os.Looper
import android.os.SystemClock
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@LargeTest
class HandlerTaskExecutorTest {
    @Test
    @Throws(InterruptedException::class)
    fun checkLooper() {
        println("test thread id=" + Thread.currentThread().id)
        val executor = HandlerTaskExecutor.withMainLooper()
        val latch = CountDownLatch(4)
        createTasks(latch, 4, 150).forEach {
            executor.postTask(it)
        }
        latch.await(1, TimeUnit.SECONDS)
        assertThat(latch.count).isEqualTo(0L)
    }

    @Suppress("SameParameterValue")
    private fun createTasks(latch: CountDownLatch, count: Int, sleepMs: Long): List<Runnable> {
        val tasks = ArrayList<Runnable>(count)
        for (i in 0 until count) {
            tasks.add(Runnable {
                println("main thread id=" + Thread.currentThread().id)
                assertThat(Looper.myLooper()).isSameInstanceAs(Looper.getMainLooper())
                SystemClock.sleep(sleepMs)
                latch.countDown()
            })
        }
        return tasks
    }
}
