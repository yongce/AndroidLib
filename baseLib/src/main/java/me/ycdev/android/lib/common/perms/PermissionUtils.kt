package me.ycdev.android.lib.common.perms

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.ArrayList

@Suppress("unused")
object PermissionUtils {
    /**
     * Check if the caller has been granted a set of permissions.
     * @return true if all permissions are already granted,
     * false if at least one permission is not yet granted.
     */
    fun hasPermissions(
        cxt: Context,
        vararg permissions: String
    ): Boolean {
        // At least one permission must be checked.
        if (permissions.isEmpty()) {
            return false
        }

        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(cxt, perm) == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    /**
     * Filter out the denied permission.
     * @return An array with length 0 will be returned if no denied permissions.
     */
    fun getDeniedPermissions(
        cxt: Context,
        vararg permissions: String
    ): Array<String> {
        val deniedPermissions = ArrayList<String>(permissions.size)
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(cxt, perm) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(perm)
            }
        }
        return deniedPermissions.toTypedArray()
    }

    /**
     * Check if all requested permissions have been granted.
     * @see Activity.onRequestPermissionsResult
     * @see FragmentActivity.onRequestPermissionsResult
     */
    fun verifyPermissions(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.isEmpty()) {
            return false
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    /**
     * Request permissions.
     */
    fun requestPermissions(
        caller: Activity,
        params: PermissionRequestParams
    ) {
        doRequestPermissions(caller, params)
    }

    /**
     * Request permissions.
     */
    fun requestPermissions(
        caller: Fragment,
        params: PermissionRequestParams
    ) {
        doRequestPermissions(caller, params)
    }

    private fun doRequestPermissions(
        caller: Any,
        params: PermissionRequestParams
    ) {
        checkCallerSupported(caller)

        var shouldShowRationale = false
        if (params.rationalePolicy == PermissionRequestParams.RATIONALE_POLICY_ON_DEMAND) {
            for (perm in params.permissions!!) {
                if (shouldShowRequestPermissionRationale(caller, perm)) {
                    shouldShowRationale = true
                    break
                }
            }
        } else if (params.rationalePolicy == PermissionRequestParams.RATIONALE_POLICY_ALWAYS) {
            shouldShowRationale = true
        }

        if (shouldShowRationale) {
            val dialog = AlertDialog.Builder(getActivity(caller))
                .setTitle(params.rationaleTitle)
                .setMessage(params.rationaleContent)
                .setPositiveButton(params.positiveBtnResId) { _, _ ->
                    doRequestPermissions(
                        caller,
                        params.permissions!!,
                        params.requestCode
                    )
                }
                .setNegativeButton(params.negativeBtnResId) { _, _ ->
                    // act as if all permissions were denied
                    params.callback!!.onRationaleDenied(params.requestCode)
                }.create()
            dialog.show()
        } else {
            doRequestPermissions(caller, params.permissions!!, params.requestCode)
        }
    }

    private fun checkCallerSupported(caller: Any) {
        if (caller !is Activity && caller !is Fragment) {
            throw IllegalArgumentException(
                "The caller must be an Activity" +
                        " or a Fragment: " + caller.javaClass.name
            )
        }
    }

    private fun shouldShowRequestPermissionRationale(
        caller: Any,
        permission: String
    ): Boolean {
        return if (caller is Activity) {
            ActivityCompat.shouldShowRequestPermissionRationale(caller, permission)
        } else (caller as? Fragment)?.shouldShowRequestPermissionRationale(permission) ?: false
    }

    private fun getActivity(caller: Any): Activity? {
        return caller as? Activity ?: if (caller is Fragment) {
            caller.activity
        } else {
            null
        }
    }

    private fun doRequestPermissions(
        caller: Any,
        perms: Array<String>,
        requestCode: Int
    ) {
        if (caller is Activity) {
            ActivityCompat.requestPermissions(caller, perms, requestCode)
        } else if (caller is Fragment) {
            caller.requestPermissions(perms, requestCode)
        }
    }
}
