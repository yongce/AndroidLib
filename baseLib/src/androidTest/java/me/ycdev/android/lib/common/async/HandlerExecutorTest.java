package me.ycdev.android.lib.common.async;

import android.os.Looper;
import android.os.SystemClock;
import androidx.test.filters.LargeTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

@LargeTest
public class HandlerExecutorTest {
    @Test
    public void checkLooper() throws InterruptedException {
        System.out.println("test thread id=" + Thread.currentThread().getId());
        HandlerExecutor executor = new HandlerExecutor(Looper.getMainLooper());
        CountDownLatch latch = new CountDownLatch(4);
        executor.postTasks(createTasks(latch, 4, 150));
        latch.await(1, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isEqualTo(0L);
    }

    @Test
    public void clearTasks() throws InterruptedException {
        HandlerExecutor executor = new HandlerExecutor(Looper.getMainLooper());
        CountDownLatch latch = new CountDownLatch(5);
        executor.postTasks(createTasks(latch, 5, 100));
        SystemClock.sleep(250);
        executor.clearTasks();
        latch.await(1, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isGreaterThan(1L);
        assertThat(latch.getCount()).isLessThan(5L);
    }

    private List<Runnable> createTasks(CountDownLatch latch, int count, long sleepMs) {
        ArrayList<Runnable> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(() -> {
                System.out.println("main thread id=" + Thread.currentThread().getId());
                assertThat(Looper.myLooper()).isSameAs(Looper.getMainLooper());
                SystemClock.sleep(sleepMs);
                latch.countDown();
            });
        }
        return tasks;
    }
}
