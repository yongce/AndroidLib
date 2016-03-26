package me.ycdev.android.lib.common.perms;

import android.support.v4.app.ActivityCompat;

public interface PermissionCallback extends ActivityCompat.OnRequestPermissionsResultCallback {
    void onRationaleDenied(int requestCode);
}
