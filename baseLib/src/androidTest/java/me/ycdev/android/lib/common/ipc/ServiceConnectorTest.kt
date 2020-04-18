package me.ycdev.android.lib.common.ipc

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.NonNull
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import me.ycdev.android.lib.common.demo.service.IDemoService
import me.ycdev.android.lib.common.demo.service.LocalServiceConnector
import me.ycdev.android.lib.common.demo.service.RemoteService
import me.ycdev.android.lib.common.demo.service.RemoteServiceConnector
import me.ycdev.android.lib.common.type.IntegerHolder
import me.ycdev.android.lib.common.utils.GcHelper
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ServiceConnectorTest {

    @Test
    @MediumTest
    fun connect_disconnect_remoteService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = RemoteServiceConnector(context)

        connectSync(connector)

        // BinderProxy

        assertThat(connector.service!!.asBinder().javaClass.name).isEqualTo("android.os.BinderProxy")
        disconnectSync(connector)
    }

    @Test
    @SmallTest
    fun connect_disconnect_localeService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = LocalServiceConnector(context)

        connectSync(connector)

        // Local object
        assertThat(connector.service!!.asBinder().javaClass.name)
            .isEqualTo("me.ycdev.android.lib.common.demo.service.LocalService\$BinderServer")
        disconnectSync(connector)
    }

    @Test
    @SmallTest
    fun getConnectLooper() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        run {
            val connector = RemoteServiceConnector(context)
            assertThat(connector.connectLooper).isEqualTo(Looper.getMainLooper())
        }

        run {
            val connector = LocalServiceConnector(context)
            assertThat(connector.connectLooper).isEqualTo(Looper.getMainLooper())
        }
    }

    @Test
    @SmallTest
    fun isServiceExist() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        run {
            val connector = RemoteServiceConnector(context)
            assertThat(connector.isServiceExist()).isTrue()
        }
        run {
            val connector = LocalServiceConnector(context)
            assertThat(connector.isServiceExist()).isTrue()
        }
        run {
            val connector = FakeServiceConnector(context)
            assertThat(connector.isServiceExist()).isFalse()
        }
        run {
            val connector = NoPermServiceConnector(context)
            assertThat(connector.isServiceExist()).isFalse()
        }
    }

    @Test
    @SmallTest
    fun selectTargetService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        run {
            val connector = RemoteServiceConnector(context)
            val servicesList = context.packageManager.queryIntentServices(
                connector.getServiceIntent(), 0
            )
            assertThat(servicesList).isNotNull()
            val cn = connector.selectTargetService(servicesList)
            assertThat(cn).isNotNull()
            assertThat(cn!!.className).isEqualTo(RemoteService::class.java.name)
        }
        run {
            val connector = NoPermServiceConnector(context)
            val servicesList = context.packageManager.queryIntentServices(
                connector.getServiceIntent(), 0
            )
            assertThat(servicesList).isNotNull()
            assertThat(connector.selectTargetService(servicesList)).isNull()
        }
    }

    @Test
    @MediumTest
    fun listeners_addAndRemove() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = RemoteServiceConnector(context)

        val latch1 = CountDownLatch(2)
        val latch2 = CountDownLatch(2)
        val listener1 = object : ConnectStateListener {
            override fun onStateChanged(newState: Int) {
                if (newState == ServiceConnector.STATE_CONNECTED || newState == ServiceConnector.STATE_DISCONNECTED) {
                    latch1.countDown()
                }
            }
        }
        val listener2 = object : ConnectStateListener {
            override fun onStateChanged(newState: Int) {
                if (newState == ServiceConnector.STATE_CONNECTED || newState == ServiceConnector.STATE_DISCONNECTED) {
                    latch2.countDown()
                }
            }
        }
        connector.addListener(listener1)
        connector.addListener(listener2)

        connector.connect()
        SystemClock.sleep(300)
        assertThat(latch1.count).isEqualTo(1)
        assertThat(latch2.count).isEqualTo(1)

        connector.removeListener(listener1)
        connector.disconnect()
        SystemClock.sleep(300)
        assertThat(latch1.count).isEqualTo(1)
        assertThat(latch2.count).isEqualTo(0)
    }

    @Test
    @LargeTest
    fun listeners_weakReference() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = RemoteServiceConnector(context)
        val objHolder = addNotReferencedListener(connector)
        GcHelper.forceGc()
        assertThat(objHolder.get()).isNull()
    }

    @Test
    @SmallTest
    fun disconnect_state_remoteService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = RemoteServiceConnector(context)
        test_disconnect_state(connector)
    }

    @Test
    @SmallTest
    fun disconnect_state_localeService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = LocalServiceConnector(context)
        test_disconnect_state(connector)
    }

    private fun test_disconnect_state(connector: ServiceConnector<*>) {
        connectSync(connector)
        val latch = CountDownLatch(1)
        val stateChangeCount = IntegerHolder(0)
        val listener = object : ConnectStateListener {
            override fun onStateChanged(newState: Int) {
                stateChangeCount.value++
                if (newState == ServiceConnector.STATE_DISCONNECTED) {
                    latch.countDown()
                }
            }
        }
        connector.addListener(listener)
        connector.disconnect()
        assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_DISCONNECTED)
        assertThat(connector.service).isNull()

        latch.await()
        assertThat(stateChangeCount.value).isEqualTo(1)
    }

    @Test
    @MediumTest
    fun waitForConnected_forever_remoteService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = RemoteServiceConnector(context)
        test_waitForConnected_forever(connector)
    }

    @Test
    @SmallTest
    fun waitForConnected_forever_localService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = LocalServiceConnector(context)
        test_waitForConnected_forever(connector)
    }

    private fun test_waitForConnected_forever(connector: ServiceConnector<*>) {
        val stateChangeCount = IntegerHolder(0)
        val latch = CountDownLatch(1);
        val listener = object : ConnectStateListener {
            override fun onStateChanged(newState: Int) {
                stateChangeCount.value++
                if (newState == ServiceConnector.STATE_CONNECTED) {
                    latch.countDown()
                }
            }
        }
        connector.addListener(listener)

        connector.waitForConnected()
        assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_CONNECTED)
        assertThat(connector.service).isNotNull()

        latch.await()
        assertThat(stateChangeCount.value).isEqualTo(2) // connecting & connected

        connector.waitForConnected()
        assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_CONNECTED)
        assertThat(stateChangeCount.value).isEqualTo(2) // already connected, no change anymore

        disconnectSync(connector)
        assertThat(stateChangeCount.value).isEqualTo(3)
    }

    @Test
    @SmallTest
    fun waitForConnected_unavailable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        run {
            val connector = FakeServiceConnector(context)
            connector.waitForConnected() // should fail immediately
            assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_DISCONNECTED)
        }
        run {
            val connector = NoPermServiceConnector(context)
            connector.waitForConnected() // should fail immediately
            assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_DISCONNECTED)
        }
    }

    @Test
    @MediumTest
    fun waitForConnected_timeout() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = ConnectDelayServiceConnector(context, 300)

        val stateChangeCount = IntegerHolder(0)
        val listener = object : ConnectStateListener {
            override fun onStateChanged(newState: Int) {
                stateChangeCount.value++
            }
        }
        connector.addListener(listener)

        connector.waitForConnected(100) //
        assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_CONNECTING)
        assertThat(stateChangeCount.value).isEqualTo(1) // connecting

        connector.waitForConnected()
        assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_CONNECTED)
        assertThat(stateChangeCount.value).isEqualTo(2) // connected

        disconnectSync(connector)
        assertThat(stateChangeCount.value).isEqualTo(3)
    }

    @Test
    @SmallTest
    fun getService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connector = RemoteServiceConnector(context)
        assertThat(connector.service).isNull()

        val listener = object : ConnectStateListener {
            override fun onStateChanged(newState: Int) {
                if (newState == ServiceConnector.STATE_CONNECTED) {
                    assertThat(connector.service).isNotNull()
                } else {
                    assertThat(connector.service).isNull()
                }
            }
        }
        connector.addListener(listener)

        connector.waitForConnected()
        assertThat(connector.service).isNotNull()

        connector.disconnect()
        assertThat(connector.service).isNull()
    }

    private class FakeServiceConnector internal constructor(cxt: Context) :
        ServiceConnector<IDemoService>(cxt, "FakeService") {

        @NonNull
        override fun getServiceIntent(): Intent {
            return Intent("me.ycdev.android.lib.common.demo.action.FAKE_SERVICE")
        }

        override fun asInterface(service: IBinder): IDemoService {
            return IDemoService.Stub.asInterface(service)
        }
    }

    private class NoPermServiceConnector internal constructor(cxt: Context) :
        RemoteServiceConnector(cxt) {

        override fun validatePermission(permission: String?): Boolean {
            return false // test no permission
        }
    }

    private class ConnectDelayServiceConnector internal constructor(
        cxt: Context,
        internal var mConnectDelay: Long
    ) : RemoteServiceConnector(cxt) {

        override fun asInterface(service: IBinder): IDemoService {
            SystemClock.sleep(mConnectDelay)
            return super.asInterface(service)
        }
    }

    companion object {
        private const val TAG = "ServiceConnectorTest"

        private fun connectSync(connector: ServiceConnector<*>) {
            val latch = CountDownLatch(1)
            val listener = object : ConnectStateListener {
                override fun onStateChanged(newState: Int) {
                    if (newState == ServiceConnector.STATE_CONNECTED) {
                        latch.countDown()
                    }
                }
            }
            connector.addListener(listener)

            assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_DISCONNECTED)
            assertThat(connector.service).isNull()

            connector.connect()
            assertThat(connector.connectState)
                .isAnyOf(ServiceConnector.STATE_CONNECTING, ServiceConnector.STATE_CONNECTED)

            try {
                latch.await()
            } catch (e: InterruptedException) {
                fail("Should not happen: $e")
            }

            assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_CONNECTED)
            assertThat(connector.service).isNotNull()
        }

        private fun disconnectSync(connector: ServiceConnector<*>) {
            assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_CONNECTED)
            assertThat(connector.service).isNotNull()

            val latch = CountDownLatch(1)
            val listener = object : ConnectStateListener {
                override fun onStateChanged(newState: Int) {
                    if (newState == ServiceConnector.STATE_DISCONNECTED) {
                        latch.countDown()
                    }
                }
            }
            connector.addListener(listener)
            connector.disconnect()
            assertThat(connector.connectState).isEqualTo(ServiceConnector.STATE_DISCONNECTED)
            assertThat(connector.service).isNull()
            try {
                latch.await()
            } catch (e: InterruptedException) {
                fail("Should not happen: $e")
            }
        }

        private fun addNotReferencedListener(
            connector: RemoteServiceConnector
        ): WeakReference<ConnectStateListener> {
            val listener = object : ConnectStateListener {
                override fun onStateChanged(newState: Int) {
                    // ignore
                }
            }
            connector.addListener(listener)
            return WeakReference(listener)
        }
    }
}
