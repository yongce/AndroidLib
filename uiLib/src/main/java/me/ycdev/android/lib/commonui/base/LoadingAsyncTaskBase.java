package me.ycdev.android.lib.commonui.base;

import android.app.Activity;

import me.ycdev.android.lib.commonui.R;

public abstract class LoadingAsyncTaskBase<Params, Result> extends
        WaitingAsyncTaskBase<Params, Integer, Result> {
    public LoadingAsyncTaskBase(Activity activity) {
        super(activity);
    }

    public LoadingAsyncTaskBase(Activity activity, boolean cancelable, boolean autoFinishWhenCanceled) {
        super(activity, cancelable, autoFinishWhenCanceled);
    }

    @Override
    protected String getInitMessage() {
        return mActivity.getString(R.string.commonui_tips_loading_percent, 0);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int percent = values[0];
        if (mDialog.isShowing()) {
            mDialog.setMessage(mActivity.getString(R.string.commonui_tips_loading_percent, percent));
        }
    }
}
