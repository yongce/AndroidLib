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
import me.ycdev.android.lib.common.utils.GcHelper;
import timber.log.Timber;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// TODO not completed
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ServiceConnectorTest {
    private static final String TAG = "ServiceConnectorTest";

    private static void connectSync(ServiceConnector connector) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        connector.addListener(newState -> {
            if (newState == ServiceConnector.STATE_CONNECTED) {
                latch.countDown();
            }
        });
        connector.connect();
        latch.await();
    }

    private static void disconnectSync(ServiceConnector connector) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        connector.addListener(newState -> {
            if (newState == ServiceConnector.STATE_DISCONNECTED) {
                latch.countDown();
            }
        });
        connector.disconnect();
        latch.await();
    }

    @Test @MediumTest
    public void remoteService_connect_disconnect() throws InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        RemoteServiceConnector connector = new RemoteServiceConnector(context);

        connectSync(connector);
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));

        // BinderProxy
        assertThat(connector.getService().asBinder().getClass().getName(),
                is("android.os.BinderProxy"));

        disconnectSync(connector);
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
    }

    @Test @SmallTest
    public void localeService_connect_disconnect() throws InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        LocalServiceConnector connector = new LocalServiceConnector(context);

        connectSync(connector);
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_CONNECTED));

        // Local object
        assertThat(connector.getService().asBinder().getClass().getName(),
                is("me.ycdev.android.lib.common.demo.service.LocalService$BinderServer"));

        disconnectSync(connector);
        assertThat(connector.getConnectState(), is(ServiceConnector.STATE_DISCONNECTED));
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
