package me.ycdev.android.lib.common.perms

import androidx.annotation.IntDef
import androidx.annotation.StringRes

class PermissionRequestParams {

    var requestCode: Int = 0
    var permissions: Array<String>? = null
    @RationalePolicy
    var rationalePolicy = RATIONALE_POLICY_ON_DEMAND
    var rationaleTitle: String? = null
    var rationaleContent: String? = null
    @StringRes
    var positiveBtnResId = android.R.string.ok
    @StringRes
    var negativeBtnResId = android.R.string.cancel
    var callback: PermissionCallback? = null

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(RATIONALE_POLICY_ON_DEMAND, RATIONALE_POLICY_NEVER, RATIONALE_POLICY_ALWAYS)
    annotation class RationalePolicy

    companion object {
        const val RATIONALE_POLICY_ON_DEMAND = 1
        const val RATIONALE_POLICY_NEVER = 2
        const val RATIONALE_POLICY_ALWAYS = 3
    }
}
