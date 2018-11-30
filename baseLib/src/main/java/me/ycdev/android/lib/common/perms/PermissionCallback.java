package me.ycdev.android.lib.common.perms;

import androidx.core.app.ActivityCompat;

public interface PermissionCallback extends ActivityCompat.OnRequestPermissionsResultCallback {
    void onRationaleDenied(int requestCode);
}
