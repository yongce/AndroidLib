package me.ycdev.android.lib.common.perms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public class PermissionUtils {
    /**
     * Check if the caller has been granted a set of permissions.
     * @return true if all permissions are already granted,
     *         false if at least one permission is not yet granted.
     */
    public static boolean hasPermissions(@NonNull Context cxt,
            @NonNull String... permissions) {
        // At least one permission must be checked.
        if (permissions.length < 1) {
            return false;
        }

        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(cxt, perm)
                    == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Filter out the denied permission.
     * @return An array with length 0 will be returned if no denied permissions.
     */
    public static String[] getDeniedPermissions(@NonNull Context cxt,
            @NonNull String... permissions) {
        ArrayList<String> deniedPermissions = new ArrayList<>(permissions.length);
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(cxt, perm)
                    == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(perm);
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * Check if all requested permissions have been granted.
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     * @see android.support.v4.app.FragmentActivity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(@NonNull int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Request permissions.
     */
    public static void requestPermissions(@NonNull Activity caller,
            @NonNull PermissionRequestParams params) {
        doRequestPermissions(caller, params);
    }

    /**
     * Request permissions.
     */
    public static void requestPermissions(@NonNull Fragment caller,
            @NonNull PermissionRequestParams params) {
        doRequestPermissions(caller, params);
    }

    private static void doRequestPermissions(final @NonNull Object caller,
            final @NonNull PermissionRequestParams params) {
        checkCallerSupported(caller);

        boolean shouldShowRationale = false;
        for (String perm : params.permissions) {
            if (shouldShowRequestPermissionRationale(caller, perm)) {
                shouldShowRationale = true;
                break;
            }
        }

        if (shouldShowRationale) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity(caller))
                    .setTitle(params.rationaleTitle)
                    .setMessage(params.rationaleContent)
                    .setPositiveButton(params.positiveBtnResId, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doRequestPermissions(caller, params.permissions, params.requestCode);
                        }
                    })
                    .setNegativeButton(params.negativeBtnResId, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // act as if all permissions were denied
                            params.callback.onRationaleDenied(params.requestCode);
                        }
                    }).create();
            dialog.show();
        } else {
            doRequestPermissions(caller, params.permissions, params.requestCode);
        }
    }

    private static void checkCallerSupported(@NonNull Object caller) {
        if (!(caller instanceof Activity) && !(caller instanceof Fragment)) {
            throw new IllegalArgumentException("The caller must be an Activity" +
                    " or a Fragment: " + caller.getClass().getName());
        }
    }

    private static boolean shouldShowRequestPermissionRationale(@NonNull Object caller,
            @NonNull String permission) {
        if (caller instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) caller, permission);
        } else if (caller instanceof Fragment) {
            return ((Fragment) caller).shouldShowRequestPermissionRationale(permission);
        } else {
            return false;
        }
    }

    private static Activity getActivity(@NonNull Object caller) {
        if (caller instanceof Activity) {
            return (Activity) caller;
        } else if (caller instanceof Fragment) {
            return ((Fragment) caller).getActivity();
        } else {
            return null;
        }
    }

    private static void doRequestPermissions(@NonNull Object caller,
            @NonNull String[] perms, int requestCode) {
        if (caller instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) caller, perms, requestCode);
        } else if (caller instanceof Fragment) {
            ((Fragment) caller).requestPermissions(perms, requestCode);
        }
    }
}
