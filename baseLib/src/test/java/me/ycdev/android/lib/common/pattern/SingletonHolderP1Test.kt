package me.ycdev.android.lib.common.pattern

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Test

class SingletonHolderP1Test {
    @Test
    fun getInstance_createsOnlyOnceAndIgnoresLaterParams() {
        val createCount = AtomicInteger(0)
        val holder = SingletonHolderP1 { value: String ->
            createCount.incrementAndGet()
            DemoSingleton(value)
        }

        val first = holder.getInstance("first")
        val second = holder.getInstance("second")

        assertThat(first).isSameInstanceAs(second)
        assertThat(first.value).isEqualTo("first")
        assertThat(createCount.get()).isEqualTo(1)
    }

    @Test
    fun getInstance_isThreadSafe() {
        val createCount = AtomicInteger(0)
        val holder = SingletonHolderP1 { value: Int ->
            createCount.incrementAndGet()
            DemoSingleton(value)
        }
        val executor = Executors.newFixedThreadPool(8)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(50)
        val instances = mutableListOf<DemoSingleton<Int>>()

        repeat(50) { index ->
            executor.execute {
                startLatch.await()
                val instance = holder.getInstance(index)
                synchronized(instances) {
                    instances.add(instance)
                }
                doneLatch.countDown()
            }
        }
        startLatch.countDown()

        assertThat(doneLatch.await(5, TimeUnit.SECONDS)).isTrue()
        executor.shutdownNow()
        assertThat(instances).hasSize(50)
        assertThat(instances.toSet()).hasSize(1)
        assertThat(createCount.get()).isEqualTo(1)
    }

    private data class DemoSingleton<T>(val value: T)
}
