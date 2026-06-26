@file:Suppress("DEPRECATION")

package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ThreadUtilsTest {
    @Test
    fun isMainThread_reportsCurrentRobolectricMainThread() {
        assertThat(ThreadUtils.isMainThread).isTrue()
    }

    @Test
    fun isMainThread_reportsFalseOnBackgroundThread() {
        val observed = AtomicBoolean(true)
        val done = CountDownLatch(1)

        Thread {
            observed.set(ThreadUtils.isMainThread)
            done.countDown()
        }.start()

        assertThat(done.await(3, TimeUnit.SECONDS)).isTrue()
        assertThat(observed.get()).isFalse()
    }

    @Test
    fun isThreadRunning_findsCurrentThreadAndRejectsFinishedThread() {
        assertThat(ThreadUtils.isThreadRunning(Thread.currentThread().id)).isTrue()

        val finishedThread = Thread {}
        finishedThread.start()
        finishedThread.join()

        assertThat(ThreadUtils.isThreadRunning(finishedThread.id)).isFalse()
        assertThat(ThreadUtils.isThreadRunning(Long.MAX_VALUE)).isFalse()
    }
}
