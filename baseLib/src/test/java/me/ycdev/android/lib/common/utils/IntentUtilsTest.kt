@file:Suppress("DEPRECATION")

package me.ycdev.android.lib.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.os.PowerManager
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class IntentUtilsTest {
    @Test
    fun canStartActivity_returnsFalseWhenNoActivityCanResolveIntent() {
        val context = RuntimeEnvironment.getApplication()

        assertThat(IntentUtils.canStartActivity(context, Intent(ACTION_TEST_ACTIVITY))).isFalse()
    }

    @Test
    fun startActivity_startsResolvedActivity() {
        val context = RuntimeEnvironment.getApplication()
        val intent = Intent(ACTION_TEST_ACTIVITY)
        shadowOf(context.packageManager).addResolveInfoForIntent(intent, activityResolveInfo())

        assertThat(IntentUtils.startActivity(context, intent)).isTrue()

        assertThat(shadowOf(context).nextStartedActivity.action).isEqualTo(ACTION_TEST_ACTIVITY)
    }

    @Test
    fun startService_returnsFalseWhenNoServiceCanResolveIntent() {
        val context = RuntimeEnvironment.getApplication()

        assertThat(IntentUtils.startService(context, Intent(ACTION_TEST_SERVICE))).isFalse()
    }

    @Test
    fun startService_setsComponentAndMarksNonForegroundServiceForLegacyTarget() {
        val context = RuntimeEnvironment.getApplication()
        val intent = Intent(ACTION_TEST_SERVICE)
        shadowOf(context.packageManager).addResolveInfoForIntent(
            intent,
            serviceResolveInfo(packageName = "example.service", className = "LegacyService", targetSdk = 25)
        )

        assertThat(IntentUtils.startService(context, intent)).isTrue()

        val startedIntent = shadowOf(context).nextStartedService
        assertThat(startedIntent.component!!.packageName).isEqualTo("example.service")
        assertThat(startedIntent.component!!.className).isEqualTo("LegacyService")
        assertThat(startedIntent.getBooleanExtra(IntentUtils.EXTRA_FOREGROUND_SERVICE, true)).isFalse()
    }

    @Test
    fun startService_marksForegroundServiceForModernTarget() {
        val context = RuntimeEnvironment.getApplication()
        val intent = Intent(ACTION_TEST_SERVICE)
        shadowOf(context.packageManager).addResolveInfoForIntent(
            intent,
            serviceResolveInfo(packageName = "example.foreground", className = "ForegroundService", targetSdk = 35)
        )

        assertThat(IntentUtils.startService(context, intent)).isTrue()

        val startedIntent = shadowOf(context).nextStartedService
        assertThat(startedIntent.component!!.packageName).isEqualTo("example.foreground")
        assertThat(startedIntent.component!!.className).isEqualTo("ForegroundService")
        assertThat(startedIntent.getBooleanExtra(IntentUtils.EXTRA_FOREGROUND_SERVICE, false)).isTrue()
    }

    @Test
    fun startForegroundService_alwaysMarksForegroundOnModernSdk() {
        val context = RuntimeEnvironment.getApplication()
        val intent = Intent(ACTION_TEST_SERVICE)
        shadowOf(context.packageManager).addResolveInfoForIntent(
            intent,
            serviceResolveInfo(packageName = "example.direct", className = "DirectForegroundService", targetSdk = 25)
        )

        assertThat(IntentUtils.startForegroundService(context, intent)).isTrue()

        val startedIntent = shadowOf(context).nextStartedService
        assertThat(startedIntent.component!!.packageName).isEqualTo("example.direct")
        assertThat(startedIntent.component!!.className).isEqualTo("DirectForegroundService")
        assertThat(startedIntent.getBooleanExtra(IntentUtils.EXTRA_FOREGROUND_SERVICE, false)).isTrue()
    }

    @Test
    fun needForegroundService_respectsSensorTargetSdkAndBatteryWhitelist() {
        val context = RuntimeEnvironment.getApplication()
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val appInfo =
            ApplicationInfo().apply {
                packageName = "example.need.foreground"
                targetSdkVersion = 35
            }

        assertThat(IntentUtils.needForegroundService(context, appInfo, listenSensor = true)).isTrue()

        shadowOf(powerManager).setIgnoringBatteryOptimizations(appInfo.packageName, true)
        assertThat(IntentUtils.needForegroundService(context, appInfo, listenSensor = false)).isFalse()

        shadowOf(powerManager).setIgnoringBatteryOptimizations(appInfo.packageName, false)
        appInfo.targetSdkVersion = 25
        assertThat(IntentUtils.needForegroundService(context, appInfo, listenSensor = false)).isFalse()
    }

    private fun activityResolveInfo(): ResolveInfo = ResolveInfo().apply {
        activityInfo =
            ActivityInfo().apply {
                packageName = "example.activity"
                name = "ExampleActivity"
            }
    }

    private fun serviceResolveInfo(
        packageName: String,
        className: String,
        targetSdk: Int
    ): ResolveInfo = ResolveInfo().apply {
        serviceInfo =
            ServiceInfo().apply {
                this.packageName = packageName
                name = className
                applicationInfo =
                    ApplicationInfo().apply {
                        this.packageName = packageName
                        targetSdkVersion = targetSdk
                    }
            }
    }

    companion object {
        private const val ACTION_TEST_ACTIVITY = "me.ycdev.android.lib.common.TEST_ACTIVITY"
        private const val ACTION_TEST_SERVICE = "me.ycdev.android.lib.common.TEST_SERVICE"
    }
}
