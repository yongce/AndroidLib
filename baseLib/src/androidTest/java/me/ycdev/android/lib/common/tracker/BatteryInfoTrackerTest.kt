package me.ycdev.android.lib.common.tracker

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BatteryInfoTrackerTest {
    @Test
    fun updateBatteryInfo_clampsPercentAndNotifiesListener() {
        val tracker = newTracker()
        val latch = CountDownLatch(1)
        var latestInfo: BatteryInfoTracker.BatteryInfo? = null
        val listener =
            object : BatteryInfoTracker.BatteryInfoListener {
                override fun onBatteryInfoUpdated(newData: BatteryInfoTracker.BatteryInfo) {
                    if (newData.voltage == 4321) {
                        latestInfo = newData
                        latch.countDown()
                    }
                }
            }

        tracker.addListener(listener)
        try {
            tracker.invokeUpdateBatteryInfo(
                Intent(Intent.ACTION_BATTERY_CHANGED).apply {
                    putExtra(BatteryManager.EXTRA_LEVEL, 150)
                    putExtra(BatteryManager.EXTRA_SCALE, 100)
                    putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING)
                    putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_USB)
                    putExtra(BatteryManager.EXTRA_VOLTAGE, 4321)
                    putExtra(BatteryManager.EXTRA_TEMPERATURE, 345)
                }
            )

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue()
            val info = latestInfo ?: error("No battery info callback received")
            assertThat(info.level).isEqualTo(150)
            assertThat(info.scale).isEqualTo(100)
            assertThat(info.percent).isEqualTo(100)
            assertThat(info.status).isEqualTo(BatteryManager.BATTERY_STATUS_CHARGING)
            assertThat(info.plugged).isEqualTo(BatteryManager.BATTERY_PLUGGED_USB)
            assertThat(info.temperature).isEqualTo(34.5)
        } finally {
            tracker.removeListener(listener)
        }
    }

    @Test
    fun updateBatteryInfo_correctsBadScaleFromHundredBasedLevel() {
        val tracker = newTracker()
        val latch = CountDownLatch(1)
        var latestInfo: BatteryInfoTracker.BatteryInfo? = null
        val listener =
            object : BatteryInfoTracker.BatteryInfoListener {
                override fun onBatteryInfoUpdated(newData: BatteryInfoTracker.BatteryInfo) {
                    if (newData.voltage == 1234) {
                        latestInfo = newData
                        latch.countDown()
                    }
                }
            }

        tracker.addListener(listener)
        try {
            tracker.invokeUpdateBatteryInfo(
                Intent(Intent.ACTION_BATTERY_CHANGED).apply {
                    putExtra(BatteryManager.EXTRA_LEVEL, 200)
                    putExtra(BatteryManager.EXTRA_SCALE, 100)
                    putExtra(BatteryManager.EXTRA_VOLTAGE, 1234)
                }
            )

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue()
            val info = latestInfo ?: error("No battery info callback received")
            assertThat(info.scale).isEqualTo(200)
            assertThat(info.percent).isEqualTo(100)
        } finally {
            tracker.removeListener(listener)
        }
    }

    private fun newTracker(): BatteryInfoTracker {
        val constructor = BatteryInfoTracker::class.java.getDeclaredConstructor(Context::class.java)
        constructor.isAccessible = true
        return constructor.newInstance(ApplicationProvider.getApplicationContext<Context>())
    }

    private fun BatteryInfoTracker.invokeUpdateBatteryInfo(intent: Intent) {
        val method = BatteryInfoTracker::class.java.getDeclaredMethod("updateBatteryInfo", Intent::class.java)
        method.isAccessible = true
        method.invoke(this, intent)
    }
}
