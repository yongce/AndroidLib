@file:Suppress("DEPRECATION")

package me.ycdev.android.lib.common.utils

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ServiceInfo
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPackageManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PackageUtilsTest {
    @Test
    fun appInfoFlags_areMappedToPackageState() {
        val appInfo =
            ApplicationInfo().apply {
                enabled = false
                flags =
                    ApplicationInfo.FLAG_SYSTEM or
                    ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or
                    ApplicationInfo.FLAG_STOPPED or
                    FLAG_PRIVILEGED
            }

        assertThat(PackageUtils.isPkgEnabled(appInfo)).isFalse()
        assertThat(PackageUtils.isPkgSystem(appInfo)).isTrue()
        assertThat(PackageUtils.isPkgPrivileged(appInfo)).isTrue()
        assertThat(PackageUtils.isPkgStopped(appInfo)).isTrue()
    }

    @Test
    fun isPkgEnabled_missingPackageDefaultsToTrue() {
        val context = RuntimeEnvironment.getApplication()

        assertThat(PackageUtils.isPkgEnabled(context, "missing.package.name")).isTrue()
    }

    @Test
    fun getAllReceivers_filtersExportedReceivers() {
        val context = RuntimeEnvironment.getApplication()
        val packageName = "example.receivers"
        shadowPackageManager().addPackage(
            PackageInfo().apply {
                this.packageName = packageName
                receivers =
                    arrayOf(
                        ActivityInfo().apply {
                            name = "PrivateReceiver"
                            exported = false
                        },
                        ActivityInfo().apply {
                            name = "ExportedReceiver"
                            exported = true
                        }
                    )
            }
        )

        assertThat(PackageUtils.getAllReceivers(context, packageName, onlyExported = false).map { it.name })
            .containsExactly("PrivateReceiver", "ExportedReceiver")
            .inOrder()
        assertThat(PackageUtils.getAllReceivers(context, packageName, onlyExported = true).map { it.name })
            .containsExactly("ExportedReceiver")
    }

    @Test
    fun getAllServicesAndActivities_returnEmptyForMissingPackage() {
        val context = RuntimeEnvironment.getApplication()

        assertThat(PackageUtils.getAllServices(context, "missing.services", onlyExported = false)).isEmpty()
        assertThat(PackageUtils.getAllActivities(context, "missing.activities", onlyExported = false)).isEmpty()
    }

    @Test
    fun getAllServices_filtersExportedServices() {
        val context = RuntimeEnvironment.getApplication()
        val packageName = "example.services"
        shadowPackageManager().addPackage(
            PackageInfo().apply {
                this.packageName = packageName
                services =
                    arrayOf(
                        ServiceInfo().apply {
                            name = "PrivateService"
                            exported = false
                        },
                        ServiceInfo().apply {
                            name = "ExportedService"
                            exported = true
                        }
                    )
            }
        )

        assertThat(PackageUtils.getAllServices(context, packageName, onlyExported = true).map { it.name })
            .containsExactly("ExportedService")
    }

    private fun shadowPackageManager(): ShadowPackageManager = org.robolectric.Shadows.shadowOf(RuntimeEnvironment.getApplication().packageManager)

    companion object {
        private const val FLAG_PRIVILEGED = 1 shl 3
    }
}
