package me.ycdev.android.lib.common.apps

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppInfoTest {
    @Test
    fun defaults_areUnsetExceptPackageName() {
        val info = AppInfo("pkg")

        assertThat(info.pkgName).isEqualTo("pkg")
        assertThat(info.appUid).isEqualTo(0)
        assertThat(info.sharedUid).isNull()
        assertThat(info.appName).isNull()
        assertThat(info.versionName).isNull()
        assertThat(info.versionCode).isEqualTo(0)
        assertThat(info.apkPath).isNull()
        assertThat(info.installTime).isEqualTo(0)
        assertThat(info.updateTime).isEqualTo(0)
        assertThat(info.isSysApp).isFalse()
        assertThat(info.isUpdatedSysApp).isFalse()
        assertThat(info.isDisabled).isFalse()
        assertThat(info.isUnmounted).isFalse()
        assertThat(info.isSelected).isFalse()
        assertThat(info.targetSdkVersion).isEqualTo(0)
        assertThat(info.minSdkVersion).isEqualTo(0)
    }

    @Test
    fun comparators_sortByExpectedFields() {
        val first = AppInfo("a.pkg").apply {
            appName = "B"
            appUid = 2
            installTime = 100
            updateTime = 200
            targetSdkVersion = 30
            minSdkVersion = 23
        }
        val second = AppInfo("b.pkg").apply {
            appName = "A"
            appUid = 1
            installTime = 200
            updateTime = 100
            targetSdkVersion = 29
            minSdkVersion = 24
        }

        assertThat(listOf(first, second).sortedWith(AppInfo.PkgNameComparator()).map { it.pkgName })
            .containsExactly("a.pkg", "b.pkg")
            .inOrder()
        assertThat(listOf(first, second).sortedWith(AppInfo.AppNameComparator()).map { it.appName })
            .containsExactly("A", "B")
            .inOrder()
        assertThat(listOf(first, second).sortedWith(AppInfo.UidComparator()).map { it.appUid })
            .containsExactly(1, 2)
            .inOrder()
        assertThat(listOf(first, second).sortedWith(AppInfo.InstallTimeComparator()).map { it.installTime })
            .containsExactly(200L, 100L)
            .inOrder()
        assertThat(listOf(first, second).sortedWith(AppInfo.UpdateTimeComparator()).map { it.updateTime })
            .containsExactly(200L, 100L)
            .inOrder()
        assertThat(listOf(first, second).sortedWith(AppInfo.TargetSdkComparator()).map { it.targetSdkVersion })
            .containsExactly(29, 30)
            .inOrder()
        assertThat(listOf(first, second).sortedWith(AppInfo.MinSdkComparator()).map { it.minSdkVersion })
            .containsExactly(23, 24)
            .inOrder()
    }

    @Test
    fun toString_containsCoreFields() {
        val info = AppInfo("pkg").apply {
            appUid = 1000
            appName = "Name"
            versionName = "1.0"
            versionCode = 2
            isSysApp = true
        }

        val text = info.toString()

        assertThat(text).contains("pkgName: pkg")
        assertThat(text).contains("appUid: 1000")
        assertThat(text).contains("appName: Name")
        assertThat(text).contains("versionName: 1.0")
        assertThat(text).contains("versionCode: 2")
        assertThat(text).contains("isSysApp: true")
    }
}
