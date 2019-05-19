package me.ycdev.android.lib.commonui.base

import android.app.Activity

import me.ycdev.android.lib.commonui.R

@Suppress("unused")
abstract class LoadingAsyncTaskBase<Params, Result> : WaitingAsyncTaskBase<Params, Int, Result> {

    override val initMessage: String
        get() = activity.getString(R.string.commonui_tips_loading_percent, 0)

    constructor(activity: Activity) : super(activity)

    constructor(activity: Activity, cancelable: Boolean, autoFinishWhenCanceled: Boolean) : super(
        activity,
        cancelable,
        autoFinishWhenCanceled
    )

    override fun onProgressUpdate(vararg values: Int?) {
        val percent = values[0]
        if (dialog.isShowing) {
            dialog.setMessage(activity.getString(R.string.commonui_tips_loading_percent, percent))
        }
    }
}
