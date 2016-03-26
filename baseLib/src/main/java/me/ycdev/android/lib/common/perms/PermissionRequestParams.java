package me.ycdev.android.lib.common.perms;

import android.support.annotation.StringRes;

public class PermissionRequestParams {
    public int requestCode;
    public String[] permissions;
    public String rationaleTitle;
    public String rationaleContent;
    public @StringRes int positiveBtnResId = android.R.string.ok;
    public @StringRes int negativeBtnResId = android.R.string.cancel;
    public PermissionCallback callback;
}
