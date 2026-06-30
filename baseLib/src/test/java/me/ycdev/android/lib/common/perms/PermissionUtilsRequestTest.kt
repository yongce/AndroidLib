package me.ycdev.android.lib.common.perms

import android.Manifest
import android.content.DialogInterface
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
class PermissionUtilsRequestTest {
    @Test
    fun requestPermissions_activityRequestsDeniedPermissionsImmediately() {
        withActivity { activity ->
            PermissionUtils.requestPermissions(
                activity,
                requestParams(
                    requestCode = 42,
                    permissions = arrayOf(Manifest.permission.READ_CONTACTS)
                )
            )

            val request = shadowOf(activity).lastRequestedPermission
            assertThat(request.requestCode).isEqualTo(42)
            assertThat(request.requestedPermissions.asList())
                .containsExactly(Manifest.permission.READ_CONTACTS)
        }
    }

    @Test
    fun requestPermissions_fragmentRequestsThroughHostActivity() {
        withActivity { activity ->
            val fragment = Fragment()
            activity.supportFragmentManager.beginTransaction().add(fragment, "fragment").commitNow()

            PermissionUtils.requestPermissions(
                fragment,
                requestParams(
                    requestCode = 7,
                    permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            )

            val request = shadowOf(activity).lastRequestedPermission
            assertThat(request.requestedPermissions.asList())
                .containsExactly(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @Test
    fun requestPermissions_rationaleNegativeButtonReportsDeniedWithoutRequesting() {
        withActivity { activity ->
            val callback = RecordingPermissionCallback()

            PermissionUtils.requestPermissions(
                activity,
                requestParams(
                    requestCode = 99,
                    permissions = arrayOf(Manifest.permission.CAMERA),
                    rationalePolicy = PermissionRequestParams.RATIONALE_POLICY_ALWAYS,
                    callback = callback
                )
            )

            val dialog = ShadowAlertDialog.getLatestAlertDialog()
            assertThat(dialog.isShowing).isTrue()

            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick()
            shadowOf(Looper.getMainLooper()).idle()

            assertThat(callback.deniedRequestCode).isEqualTo(99)
            assertThat(shadowOf(activity).lastRequestedPermission).isNull()
        }
    }

    private fun withActivity(block: (TestActivity) -> Unit) {
        val controller = Robolectric.buildActivity(TestActivity::class.java).setup()
        try {
            block(controller.get())
        } finally {
            controller.pause().stop().destroy()
        }
    }

    private fun requestParams(
        requestCode: Int,
        permissions: Array<String>,
        rationalePolicy: Int = PermissionRequestParams.RATIONALE_POLICY_NEVER,
        callback: PermissionCallback = RecordingPermissionCallback()
    ): PermissionRequestParams = PermissionRequestParams().apply {
        this.requestCode = requestCode
        this.permissions = permissions
        this.rationalePolicy = rationalePolicy
        rationaleTitle = "title"
        rationaleContent = "content"
        this.callback = callback
    }

    private class RecordingPermissionCallback : PermissionCallback {
        var deniedRequestCode: Int? = null

        override fun onRationaleDenied(requestCode: Int) {
            deniedRequestCode = requestCode
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            // No-op for request dispatch tests.
        }
    }

    class TestActivity : FragmentActivity()
}
