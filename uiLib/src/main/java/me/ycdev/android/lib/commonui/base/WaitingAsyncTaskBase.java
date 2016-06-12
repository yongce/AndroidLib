package me.ycdev.android.lib.commonui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import me.ycdev.android.lib.commonui.R;
import me.ycdev.android.lib.commonui.utils.LibConfigs;
import me.ycdev.android.lib.commonui.utils.LibLogger;

public abstract class WaitingAsyncTaskBase<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {
    private static final String TAG = "WaitingAsyncTaskBase";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    protected Activity mActivity;
    protected boolean mCancelable;
    protected boolean mAutoFinishWhenCanceled;

    protected ProgressDialog mDialog;

    public WaitingAsyncTaskBase(Activity activity) {
        this(activity, false, false);
    }

    public WaitingAsyncTaskBase(Activity activity, boolean cancelable,
            boolean autoFinishWhenCanceled) {
        mActivity = activity;
        mCancelable = cancelable;
        mAutoFinishWhenCanceled = autoFinishWhenCanceled;
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public void setAutoFinishWhenCanceled(boolean autoFinishWhenCanceled) {
        mAutoFinishWhenCanceled = autoFinishWhenCanceled;
    }

    protected String getInitMessage() {
        return mActivity.getString(R.string.commonui_tips_loading);
    }

    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(getInitMessage());
        mDialog.setCancelable(mCancelable);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (DEBUG) {
                    LibLogger.d(TAG, "to cancel, finish activity? " + mAutoFinishWhenCanceled);
                }
                cancel(true);
                if (mAutoFinishWhenCanceled) {
                    mActivity.finish();
                }
            }
        });
        mDialog.show();
    }

    @Override
    protected void onCancelled() {
        if (DEBUG) LibLogger.d(TAG, "cancelled");
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        mDialog.dismiss();
    }
}
