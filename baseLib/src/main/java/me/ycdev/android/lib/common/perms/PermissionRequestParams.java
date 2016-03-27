package me.ycdev.android.lib.common.perms;

import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PermissionRequestParams {
    public static final int RATIONALE_POLICY_ON_DEMOND = 1;
    public static final int RATIONALE_POLICY_NEVER = 2;
    public static final int RATIONALE_POLICY_ALWAYS = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        RATIONALE_POLICY_ON_DEMOND, RATIONALE_POLICY_NEVER, RATIONALE_POLICY_ALWAYS
    })
    public @interface RationalePolicy {}

    public int requestCode;
    public String[] permissions;
    public @RationalePolicy int rationalePolicy = RATIONALE_POLICY_ON_DEMOND;
    public String rationaleTitle;
    public String rationaleContent;
    public @StringRes int positiveBtnResId = android.R.string.ok;
    public @StringRes int negativeBtnResId = android.R.string.cancel;
    public PermissionCallback callback;
}
