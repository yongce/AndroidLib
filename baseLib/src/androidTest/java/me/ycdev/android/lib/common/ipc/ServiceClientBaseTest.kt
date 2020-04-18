package me.ycdev.android.lib.common.ipc

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import me.ycdev.android.lib.common.demo.service.IDemoService
import me.ycdev.android.lib.common.demo.service.LocalServiceClient
import me.ycdev.android.lib.common.demo.service.RemoteServiceClient
import me.ycdev.android.lib.common.demo.service.operation.HelloOperation
import me.ycdev.android.lib.common.utils.ThreadManager
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ServiceClientBaseTest {
    @Test
    @SmallTest
    @Throws(InterruptedException::class)
    fun workerThread() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = RemoteServiceClient(context)
        client.setAutoDisconnect(false, 0L)

        // Service not connected
        run {
            val latch = CountDownLatch(1)
            client.addOperation(object : IpcOperation<IDemoService> {
                override fun execute(service: IDemoService) {
                    assertThat(service).isNotNull()
                    assertThat(Looper.myLooper()).isSameInstanceAs(ThreadManager.instance.remoteServiceRequestIpcLooper())
                    latch.countDown()
                }
            })

            assertThat(latch.count).isEqualTo(1)
            // Waiting for service connected and operation executed
            latch.await()
        }

        // Service already connected
        run {
            val latch = CountDownLatch(1)
            client.addOperation(object : IpcOperation<IDemoService> {
                override fun execute(service: IDemoService) {
                    assertThat(service).isNotNull()
                    assertThat(Looper.myLooper()).isSameInstanceAs(ThreadManager.instance.remoteServiceRequestIpcLooper())
                    latch.countDown()
                }
            })

            assertThat(latch.count).isEqualTo(1)
            // Waiting for service connected and operation executed
            latch.await()
        }

        client.disconnect()
    }

    @Test
    @SmallTest
    fun isAutoDisconnectEnabled() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = RemoteServiceClient(context)
        assertThat(client.isAutoDisconnectEnabled).isFalse()

        client.setAutoDisconnect(true, 1000L)
        assertThat(client.isAutoDisconnectEnabled).isTrue()
    }

    @Test
    @LargeTest
    @Throws(InterruptedException::class)
    fun setAutoDisconnect_enable() {
        val disconnectTimeout: Long = 1000 // 1 second
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = RemoteServiceClient(context)
        client.setAutoDisconnect(true, disconnectTimeout)
        assertThat(client.isAutoDisconnectEnabled).isTrue()
        val timeStart: Long

        // Make the service be connected and operation be executed
        run {
            assertThat(client.serviceConnector.service).isNull()
            val latch = CountDownLatch(1)
            client.addOperation(HelloOperation("Hello, world").setNotifier(latch))
            latch.await()
            assertThat(client.serviceConnector.service).isNotNull()
            timeStart = SystemClock.elapsedRealtime()
        }

        // Waiting for the service disconnected and check the timeout
        run {
            val latch = CountDownLatch(1)
            client.serviceConnector.addListener(object : ConnectStateListener {
                override fun onStateChanged(newState: Int) {
                    if (newState == ServiceConnector.STATE_DISCONNECTED) {
                        latch.countDown()
                    }
                }
            })
            latch.await()
            val timeUsed = SystemClock.elapsedRealtime() - timeStart
            assertThat(timeUsed).isGreaterThan(disconnectTimeout)
            assertThat(timeUsed).isLessThan(disconnectTimeout + 100)
        }
    }

    @Test
    fun getServiceConnector() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = RemoteServiceClient(context)
        assertThat(client.serviceConnector).isNotNull()
    }

    @Test
    @Throws(InterruptedException::class)
    fun connect_disconnect() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = RemoteServiceClient(context)

        // connect
        run {
            val latch = CountDownLatch(1)
            client.serviceConnector.addListener(object : ConnectStateListener {
                override fun onStateChanged(newState: Int) {
                    if (newState == ServiceConnector.STATE_CONNECTED) {
                        latch.countDown()
                    }
                }
            })

            // Make sure the service will not be connected if no connect and no operations
            latch.await(500, TimeUnit.MILLISECONDS)
            assertThat(latch.count).isEqualTo(1)

            client.connect()
            latch.await(500, TimeUnit.MILLISECONDS)
            assertThat(latch.count).isEqualTo(0)
        }

        // disconnect
        run {
            val latch = CountDownLatch(1)
            client.serviceConnector.addListener(object : ConnectStateListener {
                override fun onStateChanged(newState: Int) {
                    if (newState == ServiceConnector.STATE_DISCONNECTED) {
                        latch.countDown()
                    }
                }
            })
            client.disconnect()
            latch.await()
        }
    }

    @Test
    @Throws(InterruptedException::class)
    fun addOperation_remoteService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = RemoteServiceClient(context)
        val latch = CountDownLatch(1)
        client.addOperation(HelloOperation("Hello").setNotifier(latch))
        latch.await()
    }

    @Test
    @Throws(InterruptedException::class)
    fun addOperation_localeService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = LocalServiceClient(context)
        val latch = CountDownLatch(1)
        client.addOperation(HelloOperation("Hello").setNotifier(latch))
        latch.await()
    }
}
