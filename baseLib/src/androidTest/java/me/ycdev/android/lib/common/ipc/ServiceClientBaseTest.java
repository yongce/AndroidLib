package me.ycdev.android.lib.common.ipc;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.ycdev.android.lib.common.demo.service.LocalServiceClient;
import me.ycdev.android.lib.common.demo.service.RemoteServiceClient;
import me.ycdev.android.lib.common.demo.service.operation.HelloOperation;
import me.ycdev.android.lib.common.utils.ThreadManager;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ServiceClientBaseTest {
    @Test @SmallTest
    public void workerThread() throws InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        client.setAutoDisconnect(false, 0L);

        // Service not connected
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.addOperation(service -> {
                assertThat(service, notNullValue());
                assertThat(Looper.myLooper(), is(ThreadManager.getInstance().remoteServiceRequestIpcLooper()));
                latch.countDown();
            });

            assertThat(latch.getCount(), is(1L));
            // Waiting for service connected and operation executed
            latch.await();
        }

        // Service already connected
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.addOperation(service -> {
                assertThat(service, notNullValue());
                assertThat(Looper.myLooper(), is(ThreadManager.getInstance().remoteServiceRequestIpcLooper()));
                latch.countDown();
            });

            assertThat(latch.getCount(), is(1L));
            // Waiting for service connected and operation executed
            latch.await();
        }

        client.disconnect();
    }

    @Test @SmallTest
    public void isAutoDisconnectEnabled() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        assertThat(client.isAutoDisconnectEnabled(), is(false));

        client.setAutoDisconnect(true, 1000L);
        assertThat(client.isAutoDisconnectEnabled(), is(true));
    }

    @Test @LargeTest
    public void setAutoDisconnect_enable() throws InterruptedException {
        final long DISCONNECT_TIMEOUT = 1000; // 1 second
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        client.setAutoDisconnect(true, DISCONNECT_TIMEOUT);
        assertThat(client.isAutoDisconnectEnabled(), is(true));
        long timeStart;

        // Make the service be connected and operation be executed
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.addOperation(new HelloOperation("Hello, world").setNotifier(latch));

            assertThat(client.getServiceConnector().getService(), nullValue());
            latch.await();
            assertThat(client.getServiceConnector().getService(), notNullValue());
            timeStart = SystemClock.elapsedRealtime();
        }

        // Waiting for the service disconnected and check the timeout
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.getServiceConnector().addListener(newState -> {
                if (newState == ServiceConnector.STATE_DISCONNECTED) {
                    latch.countDown();
                }
            });
            latch.await();
            long timeUsed = SystemClock.elapsedRealtime() - timeStart;
            assertThat(timeUsed, greaterThan(DISCONNECT_TIMEOUT));
            assertThat(timeUsed, lessThan(DISCONNECT_TIMEOUT + 50));
        }
    }

    @Test
    public void getServiceConnector() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        assertThat(client.getServiceConnector(), notNullValue());
    }

    @Test
    public void connect_disconnect() throws InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceClient client = new RemoteServiceClient(context);

        // connect
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.getServiceConnector().addListener(newState -> {
                if (newState == ServiceConnector.STATE_CONNECTED) {
                    latch.countDown();
                }
            });

            // Make sure the service will not be connected if no connect and no operations
            latch.await(500, TimeUnit.MILLISECONDS);
            assertThat(latch.getCount(), is(1L));

            client.connect();
            latch.await(500, TimeUnit.MILLISECONDS);
            assertThat(latch.getCount(), is(0L));
        }

        // disconnect
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.getServiceConnector().addListener(newState -> {
                if (newState == ServiceConnector.STATE_DISCONNECTED) {
                    latch.countDown();
                }
            });
            client.disconnect();
            latch.await();
        }
    }

    @Test
    public void addOperation_remoteService() throws InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        CountDownLatch latch = new CountDownLatch(1);
        client.addOperation(new HelloOperation("Hello").setNotifier(latch));
        latch.await();
    }

    @Test
    public void addOperation_localeService() throws InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        LocalServiceClient client = new LocalServiceClient(context);
        CountDownLatch latch = new CountDownLatch(1);
        client.addOperation(new HelloOperation("Hello").setNotifier(latch));
        latch.await();
    }
}
