package me.ycdev.android.lib.common.net;

import android.content.Context;
import android.os.SystemClock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import me.ycdev.android.lib.common.utils.SystemSwitchUtils;

import static com.google.common.truth.Truth.assertWithMessage;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_2G;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_3G;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_4G;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_MOBILE;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_NONE;
import static me.ycdev.android.lib.common.net.NetworkUtils.NetworkType.NETWORK_TYPE_WIFI;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NetworkUtilsTest {
    @Rule
    public Timeout timeout = Timeout.seconds(60);

    @Test
    public void test_getNetworkType() {
        // for any network
        final Context context = ApplicationProvider.getApplicationContext();
        @NetworkType int networkType = NetworkUtils.getNetworkType(context);
        assertWithMessage("check all return values")
                .that(networkType)
                .isAnyOf(NETWORK_TYPE_MOBILE, NETWORK_TYPE_WIFI, NETWORK_TYPE_NONE);

        if (SystemSwitchUtils.isWifiEnabled(context)) {
            // disable WiFi
            SystemSwitchUtils.setWifiEnabled(context, false);
            waitForWiFiConnected(context, false);
            networkType = NetworkUtils.getNetworkType(context);
            assertWithMessage("wifi disabled")
                    .that(networkType).isAnyOf(NETWORK_TYPE_MOBILE, NETWORK_TYPE_NONE);

            // enable WiFi
            SystemSwitchUtils.setWifiEnabled(context, true);
            waitForWiFiConnected(context, true);
            networkType = NetworkUtils.getNetworkType(context);
            assertWithMessage("wifi enabled")
                    .that(networkType).isEqualTo(NETWORK_TYPE_WIFI);
        } else {
            // enable WiFi
            SystemSwitchUtils.setWifiEnabled(context, true);
            waitForWiFiConnected(context, true);
            networkType = NetworkUtils.getNetworkType(context);
            assertWithMessage("wifi enabled 2")
                    .that(networkType).isEqualTo(NETWORK_TYPE_WIFI);

            // disable WiFi
            SystemSwitchUtils.setWifiEnabled(context, false);
            waitForWiFiConnected(context, false);
            networkType = NetworkUtils.getNetworkType(context);
            assertWithMessage("wifi disabled 2")
                    .that(networkType).isAnyOf(NETWORK_TYPE_MOBILE, NETWORK_TYPE_NONE);
        }
    }

    @Test
    public void test_getMobileNetworkType() {
        // for any network
        final Context context = ApplicationProvider.getApplicationContext();
        @NetworkType int networkType = NetworkUtils.getMobileNetworkType(context);
        assertWithMessage("check all return values")
                .that(networkType)
                .isAnyOf(NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G, NETWORK_TYPE_NONE);

        // disable WiFi
        SystemSwitchUtils.setWifiEnabled(context, false);
        waitForWiFiConnected(context, false);
        assertWithMessage("check all return values")
                .that(networkType)
                .isAnyOf(NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G, NETWORK_TYPE_NONE);
    }

    @Test
    public void test_getMixedNetworkType() {
        // for any network
        final Context context = ApplicationProvider.getApplicationContext();
        @NetworkType int networkType = NetworkUtils.getMixedNetworkType(context);
        assertWithMessage("check all return values").that(networkType)
                .isAnyOf(NETWORK_TYPE_WIFI, NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G, NETWORK_TYPE_NONE);
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
