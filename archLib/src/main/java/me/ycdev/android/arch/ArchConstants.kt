package me.ycdev.android.arch

import android.widget.Toast
import androidx.annotation.IntDef

object ArchConstants {

    /*
     * Durations for toast
     */
    @IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ToastDuration
}
