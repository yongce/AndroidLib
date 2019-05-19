package me.ycdev.android.arch

import android.widget.Toast
import androidx.annotation.IntDef

object ArchConstants {
    const val INTENT_TYPE_ACTIVITY = 1
    const val INTENT_TYPE_BROADCAST = 2
    const val INTENT_TYPE_SERVICE = 3

    @IntDef(INTENT_TYPE_ACTIVITY, INTENT_TYPE_BROADCAST, INTENT_TYPE_SERVICE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IntentType

    /*
     * Durations for toast
     */
    @IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ToastDuration
}
