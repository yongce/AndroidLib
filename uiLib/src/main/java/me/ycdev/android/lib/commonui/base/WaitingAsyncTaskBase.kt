package me.ycdev.android.lib.commonui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask

import me.ycdev.android.lib.common.utils.LibLogger
import me.ycdev.android.lib.commonui.R

@Suppress("MemberVisibilityCanBePrivate")
abstract class WaitingAsyncTaskBase<Params, Progress, Result> constructor(
    @field:SuppressLint("StaticFieldLeak")
    protected var activity: Activity,
    var cancelable: Boolean = false,
    protected var mAutoFinishWhenCanceled: Boolean = false
) : AsyncTask<Params, Progress, Result>() {

    protected lateinit var dialog: ProgressDialog

    protected open val initMessage: String
        get() = activity.getString(R.string.commonui_tips_loading)

    fun setAutoFinishWhenCanceled(autoFinishWhenCanceled: Boolean) {
        mAutoFinishWhenCanceled = autoFinishWhenCanceled
    }

    override fun onPreExecute() {
        dialog = ProgressDialog(activity)
        dialog.setMessage(initMessage)
        dialog.setCancelable(cancelable)
        dialog.setOnCancelListener { _ ->
            LibLogger.d(TAG, "to cancel, finish activity? $mAutoFinishWhenCanceled")
            cancel(true)
            if (mAutoFinishWhenCanceled) {
                activity.finish()
            }
        }
        dialog.show()
    }

    override fun onCancelled() {
        LibLogger.d(TAG, "cancelled")
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun onPostExecute(result: Result) {
        dialog.dismiss()
    }

    companion object {
        private const val TAG = "WaitingAsyncTaskBase"
    }
}
