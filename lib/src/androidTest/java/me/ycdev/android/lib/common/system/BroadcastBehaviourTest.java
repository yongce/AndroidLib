package me.ycdev.android.lib.common.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.ycdev.android.lib.common.utils.TestLogger;

import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class BroadcastBehaviourTest {
    private static final String TAG = "BroadcastBehaviourTest";

    @Test
    public void testAppBroadcast() {
        final Context context = InstrumentationRegistry.getContext();
        final String BROADCAST_ACTION = context.getPackageName()
                + ".action.DYNAMIC_BROADCAST_TEST";
        final String BROADCAST_PERM = context.getPackageName()
                + ".permission.DYNAMIC_BROADCAST";

        final CountDownLatch latch1 = new CountDownLatch(2);
        final CountDownLatch latchWifi1 = new CountDownLatch(1);
        BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                TestLogger.d(TAG, "receiver1: " + action);
                if (action.equals(BROADCAST_ACTION)) {
                    latch1.countDown();
                } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    latchWifi1.countDown();
                }
            }
        };

        final CountDownLatch latch2 = new CountDownLatch(2);
        final CountDownLatch latchWifi2 = new CountDownLatch(1);
        BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                TestLogger.d(TAG, "receiver2: " + action);
                if (action.equals(BROADCAST_ACTION)) {
                    latch2.countDown();
                } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    latchWifi2.countDown();
                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        final Intent intent = new Intent(BROADCAST_ACTION);


        // register receivers
        context.registerReceiver(mReceiver1, filter, BROADCAST_PERM, null);
        context.registerReceiver(mReceiver2, filter);

        // send app broadcast with permission
        context.sendBroadcast(intent, BROADCAST_PERM);
        // send app broadcast without permission
        context.sendBroadcast(intent);
        // trigger system broadcast
        toggleWifiState(context);

        try {
            latch1.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (latch1.getCount() != 0) {
            fail("failed to receive the app broadcast#1");
        }

        try {
            latch2.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (latch2.getCount() != 0) {
            fail("failed to receive the app broadcast#2");
        }

        try {
            latchWifi1.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (latchWifi1.getCount() != 0) {
            fail("failed to receive the wifi broadcast#1");
        }

        try {
            latchWifi2.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (latchWifi2.getCount() != 0) {
            fail("failed to receive the wifi broadcast#2");
        }

        // unregister receivers
        context.unregisterReceiver(mReceiver1);
        context.unregisterReceiver(mReceiver2);
        // restore the wifi state
        toggleWifiState(context);
    }

    private void toggleWifiState(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_DISABLED
                || wifiState == WifiManager.WIFI_STATE_DISABLING) {
            wifiMgr.setWifiEnabled(true);
        } else {
            wifiMgr.setWifiEnabled(false);
        }
    }
}
