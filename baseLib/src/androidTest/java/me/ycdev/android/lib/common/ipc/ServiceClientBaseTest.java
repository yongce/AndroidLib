package me.ycdev.android.lib.common.ipc;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.ycdev.android.lib.common.demo.service.LocalServiceClient;
import me.ycdev.android.lib.common.demo.service.RemoteServiceClient;
import me.ycdev.android.lib.common.demo.service.operation.HelloOperation;
import me.ycdev.android.lib.common.utils.ThreadManager;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ServiceClientBaseTest {
    @Test @SmallTest
    public void workerThread() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        client.setAutoDisconnect(false, 0L);

        // Service not connected
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.addOperation(service -> {
                assertThat(service).isNotNull();
                assertThat(Looper.myLooper()).isSameAs(ThreadManager.getInstance().remoteServiceRequestIpcLooper());
                latch.countDown();
            });

            assertThat(latch.getCount()).isEqualTo(1);
            // Waiting for service connected and operation executed
            latch.await();
        }

        // Service already connected
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.addOperation(service -> {
                assertThat(service).isNotNull();
                assertThat(Looper.myLooper()).isSameAs(ThreadManager.getInstance().remoteServiceRequestIpcLooper());
                latch.countDown();
            });

            assertThat(latch.getCount()).isEqualTo(1);
            // Waiting for service connected and operation executed
            latch.await();
        }

        client.disconnect();
    }

    @Test @SmallTest
    public void isAutoDisconnectEnabled() {
        Context context = ApplicationProvider.getApplicationContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        assertThat(client.isAutoDisconnectEnabled()).isFalse();

        client.setAutoDisconnect(true, 1000L);
        assertThat(client.isAutoDisconnectEnabled()).isTrue();
    }

    @Test @LargeTest
    public void setAutoDisconnect_enable() throws InterruptedException {
        final long DISCONNECT_TIMEOUT = 1000; // 1 second
        Context context = ApplicationProvider.getApplicationContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        client.setAutoDisconnect(true, DISCONNECT_TIMEOUT);
        assertThat(client.isAutoDisconnectEnabled()).isTrue();
        long timeStart;

        // Make the service be connected and operation be executed
        {
            CountDownLatch latch = new CountDownLatch(1);
            client.addOperation(new HelloOperation("Hello, world").setNotifier(latch));

            assertThat(client.getServiceConnector().getService()).isNull();
            latch.await();
            assertThat(client.getServiceConnector().getService()).isNotNull();
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
            assertThat(timeUsed).isGreaterThan(DISCONNECT_TIMEOUT);
            assertThat(timeUsed).isLessThan(DISCONNECT_TIMEOUT + 50);
        }
    }

    @Test
    public void getServiceConnector() {
        Context context = ApplicationProvider.getApplicationContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        assertThat(client.getServiceConnector()).isNotNull();
    }

    @Test
    public void connect_disconnect() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
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
            assertThat(latch.getCount()).isEqualTo(1);

            client.connect();
            latch.await(500, TimeUnit.MILLISECONDS);
            assertThat(latch.getCount()).isEqualTo(0);
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
        Context context = ApplicationProvider.getApplicationContext();
        RemoteServiceClient client = new RemoteServiceClient(context);
        CountDownLatch latch = new CountDownLatch(1);
        client.addOperation(new HelloOperation("Hello").setNotifier(latch));
        latch.await();
    }

    @Test
    public void addOperation_localeService() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        LocalServiceClient client = new LocalServiceClient(context);
        CountDownLatch latch = new CountDownLatch(1);
        client.addOperation(new HelloOperation("Hello").setNotifier(latch));
        latch.await();
    }
}
