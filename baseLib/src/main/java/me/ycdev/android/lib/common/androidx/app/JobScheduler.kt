@file:Suppress("unused")

package me.ycdev.android.lib.common.androidx.app

import android.app.job.JobScheduler

fun JobScheduler.isJobScheduled(jobId: Int): Boolean {
    return getPendingJob(jobId) != null
}
