package me.ycdev.android.arch.wrapper

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import me.ycdev.android.arch.ArchConstants.ToastDuration

/**
 * A wrapper class for Toast so that we can customize and unify the UI in future.
 */
object ToastHelper {

    fun show(cxt: Context, @StringRes msgResId: Int, @ToastDuration duration: Int) {
        Toast.makeText(cxt, msgResId, duration).show()
    }

    fun show(cxt: Context, msg: CharSequence, @ToastDuration duration: Int) {
        Toast.makeText(cxt, msg, duration).show()
    }
}
