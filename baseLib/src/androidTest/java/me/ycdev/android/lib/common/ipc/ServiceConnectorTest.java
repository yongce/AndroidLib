package me.ycdev.android.lib.common.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import me.ycdev.android.lib.common.demo.service.IDemoService;
import me.ycdev.android.lib.common.demo.service.LocalServiceConnector;
import me.ycdev.android.lib.common.demo.service.RemoteService;
import me.ycdev.android.lib.common.demo.service.RemoteServiceConnector;
import me.ycdev.android.lib.common.type.BooleanHolder;
import me.ycdev.android.lib.common.type.IntegerHolder;
import me.ycdev.android.lib.common.utils.GcHelper;
import timber.log.Timber;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ServiceConnectorTest {
    private static final String TAG = "ServiceConnectorTest";

    private static void connectSync(ServiceConnector connector) {
        CountDownLatch latch = new CountDownLatch(1);
        connector.addListener(newState -> {
            if (newState == ServiceConnector.STATE_CONNECTED) {
                latch.countDown();
            }
        });

        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
        assertThat(connector.getService(), nullValue());

        connector.connect();
        assertThat(connector.getConnectState(), anyOf(is(ServiceConnector.STATE_CONNECTING),
                is(ServiceConnector.STATE_CONNECTED)));

        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Should not happen: " + e);
        }
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));
        assertThat(connector.getService(), notNullValue());
    }

    private static void disconnectSync(ServiceConnector connector) {
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));
        assertThat(connector.getService(), notNullValue());

        CountDownLatch latch = new CountDownLatch(1);
        connector.addListener(newState -> {
            if (newState == ServiceConnector.STATE_DISCONNECTED) {
                latch.countDown();
            }
        });
        connector.disconnect();
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
        assertThat(connector.getService(), nullValue());
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Should not happen: " + e);
        }
    }

    @Test @MediumTest
    public void connect_disconnect_remoteService() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);

        connectSync(connector);

        // BinderProxy
        assertThat(connector.getService().asBinder().getClass().getName(),
                is("android.os.BinderProxy"));

        disconnectSync(connector);
    }

    @Test @SmallTest
    public void connect_disconnect_localeService() {
        Context context = InstrumentationRegistry.getContext();
        LocalServiceConnector connector = new LocalServiceConnector(context);

        connectSync(connector);

        // Local object
        assertThat(connector.getService().asBinder().getClass().getName(),
                is("me.ycdev.android.lib.common.demo.service.LocalService$BinderServer"));

        disconnectSync(connector);
    }

    @Test @SmallTest
    public void getConnectLooper() {
        Context context = InstrumentationRegistry.getContext();
        {
            RemoteServiceConnector connector = new RemoteServiceConnector(context);
            assertThat(connector.getConnectLooper(), is(Looper.getMainLooper()));
        }

        {
            LocalServiceConnector connector = new LocalServiceConnector(context);
            assertThat(connector.getConnectLooper(), is(Looper.getMainLooper()));
        }
    }

    @Test @SmallTest
    public void isServiceExist() {
        Context context = InstrumentationRegistry.getContext();
        {
            RemoteServiceConnector connector = new RemoteServiceConnector(context);
            assertThat(connector.isServiceExist(), is(true));
        }
        {
            LocalServiceConnector connector = new LocalServiceConnector(context);
            assertThat(connector.isServiceExist(), is(true));
        }
        {
            FakeServiceConnector connector = new FakeServiceConnector(context);
            assertThat(connector.isServiceExist(), is(false));
        }
        {
            NoPermServiceConnector connector = new NoPermServiceConnector(context);
            assertThat(connector.isServiceExist(), is(false));
        }
    }

    @Test @SmallTest
    public void selectTargetService() {
        Context context = InstrumentationRegistry.getContext();
        {
            ServiceConnector connector = new RemoteServiceConnector(context);
            List<ResolveInfo> servicesList = context.getPackageManager().queryIntentServices(
                    connector.getServiceIntent(), 0);
            assertThat(servicesList, notNullValue());
            ComponentName cn = connector.selectTargetService(servicesList);
            assertThat(cn, notNullValue());
            assertThat(cn.getClassName(), is(RemoteService.class.getName()));
        }
        {
            ServiceConnector connector = new NoPermServiceConnector(context);
            List<ResolveInfo> servicesList = context.getPackageManager().queryIntentServices(
                    connector.getServiceIntent(), 0);
            assertThat(servicesList, notNullValue());
            assertThat(connector.selectTargetService(servicesList), nullValue());
        }
    }

    @Test @MediumTest
    public void listeners_addAndRemove() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);

        CountDownLatch latch1 = new CountDownLatch(2);
        CountDownLatch latch2 = new CountDownLatch(2);
        ConnectStateListener listener1 = newState -> {
            if (newState == ServiceConnector.STATE_CONNECTED
                    || newState == ServiceConnector.STATE_DISCONNECTED) {
                latch1.countDown();
            }
        };
        ConnectStateListener listener2 = newState -> {
            if (newState == ServiceConnector.STATE_CONNECTED
                    || newState == ServiceConnector.STATE_DISCONNECTED) {
                latch2.countDown();
            }
        };
        connector.addListener(listener1);
        connector.addListener(listener2);

        connector.connect();
        SystemClock.sleep(300);
        assertThat(latch1.getCount(), is(1L));
        assertThat(latch2.getCount(), is(1L));

        connector.removeListener(listener1);
        connector.disconnect();
        SystemClock.sleep(300);
        assertThat(latch1.getCount(), is(1L));
        assertThat(latch2.getCount(), is(0L));
    }

    @Test @LargeTest
    public void listeners_weakReference() throws IOException {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);
        BooleanHolder gcState = new BooleanHolder(false);
        // Must use another method to add the listener. Don't know why!
        addNotReferencedListener(connector, gcState);
        GcHelper.forceGc(gcState);
    }

    private static void addNotReferencedListener(RemoteServiceConnector connector, BooleanHolder gcState) {
        ConnectStateListener listener = new GcMonitorConnectStateListener(gcState);
        connector.addListener(listener);
    }

    @Test @SmallTest
    public void disconnect_state_remoteService() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);
        test_disconnect_state(connector);
    }

    @Test @SmallTest
    public void disconnect_state_localeService() {
        Context context = InstrumentationRegistry.getContext();
        LocalServiceConnector connector = new LocalServiceConnector(context);
        test_disconnect_state(connector);
    }

    private void test_disconnect_state(ServiceConnector connector) {
        connectSync(connector);
        IntegerHolder stateChangeCount = new IntegerHolder(0);
        connector.addListener(newState -> stateChangeCount.value++);
        connector.disconnect();
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
        assertThat(connector.getService(), nullValue());
        assertThat(stateChangeCount.value, is(0));
    }

    @Test @MediumTest
    public void waitForConnected_forever_remoteService() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);
        test_waitForConnected_forever(connector);
    }

    @Test @SmallTest
    public void waitForConnected_forever_localService() {
        Context context = InstrumentationRegistry.getContext();
        LocalServiceConnector connector = new LocalServiceConnector(context);
        test_waitForConnected_forever(connector);
    }

    private void test_waitForConnected_forever(ServiceConnector connector) {
        IntegerHolder stateChangeCount = new IntegerHolder(0);
        connector.addListener(newState -> stateChangeCount.value++);

        connector.waitForConnected();
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));
        assertThat(connector.getService(), notNullValue());
        assertThat(stateChangeCount.value, is(2)); // connecting & connected

        connector.waitForConnected();
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));
        assertThat(stateChangeCount.value, is(2)); // already connected, no change anymore

        disconnectSync(connector);
        assertThat(stateChangeCount.value, is(3));
    }

    @Test @SmallTest
    public void waitForConnected_unavailable() {
        Context context = InstrumentationRegistry.getContext();
        {
            FakeServiceConnector connector = new FakeServiceConnector(context);
            connector.waitForConnected(); // should fail immediately
            assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
        }
        {
            NoPermServiceConnector connector = new NoPermServiceConnector(context);
            connector.waitForConnected(); // should fail immediately
            assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
        }
    }

    @Test @MediumTest
    public void waitForConnected_timeout() {
        Context context = InstrumentationRegistry.getContext();
        ConnectDelayServiceConnector connector = new ConnectDelayServiceConnector(context, 300);

        IntegerHolder stateChangeCount = new IntegerHolder(0);
        connector.addListener(newState -> stateChangeCount.value++);

        connector.waitForConnected(100); //
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTING));
        assertThat(stateChangeCount.value, is(1)); // connecting

        connector.waitForConnected();
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));
        assertThat(stateChangeCount.value, is(2)); // connected

        disconnectSync(connector);
        assertThat(stateChangeCount.value, is(3));
    }

    @Test @SmallTest
    public void getService() {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);
        assertThat(connector.getService(), nullValue());

        connector.addListener(newState -> {
            if (newState == ServiceConnector.STATE_CONNECTED) {
                assertThat(connector.getService(), notNullValue());
            } else {
                assertThat(connector.getService(), nullValue());
            }
        });

        connector.waitForConnected();
        assertThat(connector.getService(), notNullValue());

        connector.disconnect();
        assertThat(connector.getService(), nullValue());
    }

    private static class FakeServiceConnector extends ServiceConnector<IDemoService> {
        FakeServiceConnector(Context cxt) {
            super(cxt, "FakeService");
        }

        @NonNull
        @Override
        protected Intent getServiceIntent() {
            return new Intent("me.ycdev.android.lib.common.demo.action.FAKE_SERVICE");
        }

        @Override
        protected IDemoService asInterface(IBinder service) {
            return null;
        }
    }

    private static class NoPermServiceConnector extends RemoteServiceConnector {
        NoPermServiceConnector(Context cxt) {
            super(cxt);
        }

        @Override
        protected boolean validatePermission(String permission) {
            return false; // test no permission
        }
    }

    private static class ConnectDelayServiceConnector extends RemoteServiceConnector {
        long mConnectDelay;

        ConnectDelayServiceConnector(Context cxt, long delay) {
            super(cxt);
            mConnectDelay = delay;
        }

        @Override
        protected IDemoService asInterface(IBinder service) {
            SystemClock.sleep(mConnectDelay);
            return super.asInterface(service);
        }
    }

    private static class GcMonitorConnectStateListener implements ConnectStateListener {
        private BooleanHolder mGcState;

        GcMonitorConnectStateListener(BooleanHolder gcState) {
            mGcState = gcState;
        }

        @Override
        public void onStateChanged(int newState) {
            Timber.tag(TAG).d("GcMonitorConnectStateListener, state changed: %s",
                    ServiceConnector.strConnectState(newState));
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            Timber.tag(TAG).d("GcMonitorConnectStateListener, collected by GC");
            mGcState.value = true;
        }
    }
}
