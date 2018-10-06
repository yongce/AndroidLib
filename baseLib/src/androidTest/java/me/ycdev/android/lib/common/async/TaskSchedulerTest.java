package me.ycdev.android.lib.common.async;

import android.support.test.filters.LargeTest;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// TODO not completed
@LargeTest
public class TaskSchedulerTest {
    @Test @LargeTest
    public void basic() throws InterruptedException {
        ITaskExecutor taskExecutor = new HandlerThreadExecutor("test");
        TaskScheduler taskScheduler = new TaskScheduler(taskExecutor, "test");

        // add an one-off task
        {
            CountDownLatch latch = new CountDownLatch(1);
            taskScheduler.schedule(latch::countDown, 1000);
            latch.await(2, TimeUnit.SECONDS);
            assertThat(latch.getCount(), is(0L));
        }

        // add a period task
        {
            CountDownLatch latch = new CountDownLatch(2);
            taskScheduler.schedule(latch::countDown, 0, 2000);
            latch.await(5, TimeUnit.SECONDS);
            assertThat(latch.getCount(), is(0L));
        }
    }
}
