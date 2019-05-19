package me.ycdev.android.lib.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

object IntentUtils {
    fun canStartActivity(cxt: Context, activityIntent: Intent): Boolean {
        // Use PackageManager.MATCH_DEFAULT_ONLY to behavior same as Context#startAcitivty()
        val resolveInfo = cxt.packageManager.resolveActivity(
            activityIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfo != null
    }
}
