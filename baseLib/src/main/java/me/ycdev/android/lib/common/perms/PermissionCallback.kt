package me.ycdev.android.lib.common.perms

import androidx.core.app.ActivityCompat

interface PermissionCallback : ActivityCompat.OnRequestPermissionsResultCallback {
    fun onRationaleDenied(requestCode: Int)
}
