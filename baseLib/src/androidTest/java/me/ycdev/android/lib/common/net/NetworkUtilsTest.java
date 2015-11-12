package me.ycdev.android.lib.common.net;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import me.ycdev.android.lib.common.utils.SystemSwitchUtils;

import static me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_2G;
import static me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_3G;
import static me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_4G;
import static me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_MOBILE;
import static me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_NONE;
import static me.ycdev.android.lib.common.net.NetworkUtils.NETWORK_TYPE_WIFI;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NetworkUtilsTest {
    @Rule
    public Timeout timeout = Timeout.seconds(60);

    @Test
    public void test_getNetworkType() {
        // for any network
        final Context context = InstrumentationRegistry.getContext();
        @NetworkType int networkType = NetworkUtils.getNetworkType(context);
        assertThat("check all return values", networkType,
                anyOf(equalTo(NETWORK_TYPE_MOBILE), equalTo(NETWORK_TYPE_WIFI),
                        equalTo(NETWORK_TYPE_NONE)));

        if (SystemSwitchUtils.isWifiEnabled(context)) {
            // disable WiFi
            SystemSwitchUtils.setWifiEnabled(context, false);
            waitForWiFiConnected(context, false);
            networkType = NetworkUtils.getNetworkType(context);
            assertThat("wifi disabled", networkType,
                    anyOf(equalTo(NETWORK_TYPE_MOBILE), equalTo(NETWORK_TYPE_NONE)));

            // enable WiFi
            SystemSwitchUtils.setWifiEnabled(context, true);
            waitForWiFiConnected(context, true);
            networkType = NetworkUtils.getNetworkType(context);
            assertThat("wifi enabled", networkType, is(NETWORK_TYPE_WIFI));
        } else {
            // enable WiFi
            SystemSwitchUtils.setWifiEnabled(context, true);
            waitForWiFiConnected(context, true);
            networkType = NetworkUtils.getNetworkType(context);
            assertThat("wifi enabled 2", networkType, is(NETWORK_TYPE_WIFI));

            // disable WiFi
            SystemSwitchUtils.setWifiEnabled(context, false);
            waitForWiFiConnected(context, false);
            networkType = NetworkUtils.getNetworkType(context);
            assertThat("wifi disabled 2", networkType,
                    anyOf(equalTo(NETWORK_TYPE_MOBILE), equalTo(NETWORK_TYPE_NONE)));
        }
    }

    @Test
    public void test_getMobileNetworkType() {
        // for any network
        final Context context = InstrumentationRegistry.getContext();
        @NetworkType int networkType = NetworkUtils.getMobileNetworkType(context);
        assertThat("check all return values", networkType,
                anyOf(equalTo(NETWORK_TYPE_2G), equalTo(NETWORK_TYPE_3G),
                        equalTo(NETWORK_TYPE_4G), equalTo(NETWORK_TYPE_NONE)));

        // disable WiFi
        SystemSwitchUtils.setWifiEnabled(context, false);
        waitForWiFiConnected(context, false);
        assertThat("check all return values",
                networkType, anyOf(equalTo(NETWORK_TYPE_2G), equalTo(NETWORK_TYPE_3G),
                        equalTo(NETWORK_TYPE_4G), equalTo(NETWORK_TYPE_NONE)));
    }

    @Test
    public void test_getMixedNetworkType() {
        // for any network
        final Context context = InstrumentationRegistry.getContext();
        @NetworkType int networkType = NetworkUtils.getMixedNetworkType(context);
        assertThat("check all return values", networkType,
                anyOf(equalTo(NETWORK_TYPE_WIFI), equalTo(NETWORK_TYPE_2G),
                        equalTo(NETWORK_TYPE_3G), equalTo(NETWORK_TYPE_4G),
                        equalTo(NETWORK_TYPE_NONE)));
    }

    @Test
    public void test_isActiveNetworkMetered() {
        // TODO
    }

    @Test
    public void test_openHttpURLConnection() {
        // TODO
    }

    private void waitForWiFiConnected(Context cxt, boolean connected) {
        while (true) {
            if (connected && NetworkUtils.getNetworkType(cxt) == NETWORK_TYPE_WIFI) {
                break;
            } else if (!connected && NetworkUtils.getNetworkType(cxt) != NETWORK_TYPE_WIFI) {
                break;
            }
            SystemClock.sleep(100);
        }
    }
}
