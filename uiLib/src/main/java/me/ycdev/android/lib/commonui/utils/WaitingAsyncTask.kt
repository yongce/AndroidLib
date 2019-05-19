package me.ycdev.android.lib.commonui.utils

import android.app.Activity
import android.os.SystemClock

import me.ycdev.android.lib.commonui.base.WaitingAsyncTaskBase

class WaitingAsyncTask(
    activity: Activity,
    protected override val initMessage: String,
    private val mTask: Runnable
) : WaitingAsyncTaskBase<Void, Void, Void>(activity) {

    override fun doInBackground(vararg params: Void): Void? {
        val timeStart = SystemClock.elapsedRealtime()
        mTask.run()
        val timeUsed = SystemClock.elapsedRealtime() - timeStart
        if (timeUsed < WAITING_TIME_MIN) {
            SystemClock.sleep(WAITING_TIME_MIN - timeUsed)
        }
        return null
    }

    companion object {
        private val WAITING_TIME_MIN: Long = 500 // ms
    }
}
