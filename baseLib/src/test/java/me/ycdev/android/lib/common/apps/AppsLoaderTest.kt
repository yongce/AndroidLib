@file:Suppress("DEPRECATION")

package me.ycdev.android.lib.common.apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AppsLoaderTest {
    @Test
    fun loadInstalledApps_filtersUnmountedDisabledAndSystemApps() {
        val context = RuntimeEnvironment.getApplication()
        val mountedUser = addPackage(context, "example.loader.user")
        val unmounted = addPackage(context, "example.loader.unmounted", sourceFile = null)
        val disabled = addPackage(context, "example.loader.disabled", enabled = false)
        val system =
            addPackage(
                context,
                "example.loader.system",
                flags = ApplicationInfo.FLAG_SYSTEM
            )
        val updatedSystem =
            addPackage(
                context,
                "example.loader.updated.system",
                flags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
            )
        val filter =
            AppsLoadFilter().apply {
                onlyMounted = true
                onlyEnabled = true
                includeSysApp = false
                includeUpdatedSysApp = false
            }

        val packageNames =
            AppsLoader.getInstance(context)
                .loadInstalledApps(filter, AppsLoadConfig(loadLabel = false, loadIcon = false), listener = null)
                .map { it.pkgName }

        assertThat(packageNames).contains(mountedUser)
        assertThat(packageNames).doesNotContain(unmounted)
        assertThat(packageNames).doesNotContain(disabled)
        assertThat(packageNames).doesNotContain(system)
        assertThat(packageNames).doesNotContain(updatedSystem)
    }

    @Test
    fun loadInstalledApps_keepsUpdatedSystemAppsWhenConfigured() {
        val context = RuntimeEnvironment.getApplication()
        val updatedSystem =
            addPackage(
                context,
                "example.loader.updated.system.allowed",
                flags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
            )
        val filter =
            AppsLoadFilter().apply {
                includeSysApp = false
                includeUpdatedSysApp = true
            }

        val packageNames =
            AppsLoader.getInstance(context)
                .loadInstalledApps(filter, AppsLoadConfig(loadLabel = false, loadIcon = false), listener = null)
                .map { it.pkgName }

        assertThat(packageNames).contains(updatedSystem)
    }

    @Test
    fun loadInstalledApps_excludesSelfWhenConfigured() {
        val context = RuntimeEnvironment.getApplication()
        addPackage(context, context.packageName)
        val filter =
            AppsLoadFilter().apply {
                includeMyself = false
            }

        val packageNames =
            AppsLoader.getInstance(context)
                .loadInstalledApps(filter, AppsLoadConfig(loadLabel = false, loadIcon = false), listener = null)
                .map { it.pkgName }

        assertThat(packageNames).doesNotContain(context.packageName)
    }

    @Test
    fun loadInstalledApps_respectsLabelAndIconLoadingFlags() {
        val context = RuntimeEnvironment.getApplication()
        val packageName = addPackage(context, "example.loader.no.heavy.fields", label = "  Demo Label")

        val app =
            AppsLoader.getInstance(context)
                .loadInstalledApps(
                    AppsLoadFilter(),
                    AppsLoadConfig(loadLabel = false, loadIcon = false),
                    listener = null
                )
                .single { it.pkgName == packageName }

        assertThat(app.appName).isNull()
        assertThat(app.appIcon).isNull()

        val appWithLabel =
            AppsLoader.getInstance(context)
                .loadInstalledApps(
                    AppsLoadFilter(),
                    AppsLoadConfig(loadLabel = true, loadIcon = false),
                    listener = null
                )
                .single { it.pkgName == packageName }

        assertThat(appWithLabel.appName).isEqualTo("Demo Label")
        assertThat(appWithLabel.appIcon).isNull()
    }

    @Test
    fun loadInstalledApps_reportsProgressAndStopsAfterCancellation() {
        val context = RuntimeEnvironment.getApplication()
        addPackage(context, "example.loader.cancel.one")
        addPackage(context, "example.loader.cancel.two")
        val listener = CancellingListener()

        val apps =
            AppsLoader.getInstance(context)
                .loadInstalledApps(
                    AppsLoadFilter(),
                    AppsLoadConfig(loadLabel = false, loadIcon = false),
                    listener
                )

        assertThat(listener.progressValues).hasSize(1)
        assertThat(listener.progressValues.single()).isAtLeast(1)
        assertThat(apps).hasSize(1)
    }

    private fun addPackage(
        context: Context,
        packageName: String,
        flags: Int = 0,
        enabled: Boolean = true,
        sourceFile: File? = File.createTempFile(packageName, ".apk", context.cacheDir),
        label: String = packageName
    ): String {
        val appInfo =
            ApplicationInfo().apply {
                this.packageName = packageName
                this.flags = flags
                this.enabled = enabled
                this.sourceDir = sourceFile?.absolutePath
                this.publicSourceDir = sourceFile?.absolutePath
                this.nonLocalizedLabel = label
                this.targetSdkVersion = 35
                this.minSdkVersion = 24
            }
        shadowOf(context.packageManager).addPackage(
            PackageInfo().apply {
                this.packageName = packageName
                this.applicationInfo = appInfo
                this.versionName = "1.0"
                this.versionCode = 1
                this.firstInstallTime = 10
                this.lastUpdateTime = 20
            }
        )
        if (!enabled) {
            context.packageManager.setApplicationEnabledSetting(
                packageName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                0
            )
        }
        return packageName
    }

    private class CancellingListener : AppsLoadListener {
        val progressValues = mutableListOf<Int>()
        private var cancelled = false

        override fun isCancelled(): Boolean = cancelled

        override fun onProgressUpdated(
            percent: Int,
            appInfo: AppInfo
        ) {
            progressValues.add(percent)
            cancelled = true
        }
    }
}
