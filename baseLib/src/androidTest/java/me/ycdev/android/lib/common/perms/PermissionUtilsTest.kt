package me.ycdev.android.lib.common.perms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionUtilsTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun hasPermissions_requiresAtLeastOnePermission() {
        assertThat(PermissionUtils.hasPermissions(context)).isFalse()
    }

    @Test
    fun hasPermissions_returnsFalseWhenAnyPermissionDenied() {
        assertThat(PermissionUtils.hasPermissions(context, Manifest.permission.READ_CONTACTS)).isFalse()
    }

    @Test
    fun getDeniedPermissions_returnsOnlyDeniedPermissionsInOrder() {
        val denied =
            PermissionUtils.getDeniedPermissions(
                context,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        assertThat(denied.asList())
            .containsExactly(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .inOrder()
    }

    @Test
    fun verifyPermissions_requiresAtLeastOneResult() {
        assertThat(PermissionUtils.verifyPermissions(intArrayOf())).isFalse()
    }

    @Test
    fun verifyPermissions_returnsTrueOnlyWhenAllGranted() {
        assertThat(PermissionUtils.verifyPermissions(intArrayOf(PackageManager.PERMISSION_GRANTED))).isTrue()
        assertThat(
            PermissionUtils.verifyPermissions(
                intArrayOf(
                    PackageManager.PERMISSION_GRANTED,
                    PackageManager.PERMISSION_DENIED
                )
            )
        ).isFalse()
    }
}
