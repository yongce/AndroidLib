@file:Suppress("unused")

package me.ycdev.android.lib.common.androidx.app

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

fun JobScheduler.getPendingJobCompat(jobId: Int): JobInfo? {
    if (VERSION.SDK_INT >= VERSION_CODES.N) {
        return getPendingJob(jobId)
    } else {
        val jobInfoList = allPendingJobs
        for (jobInfo in jobInfoList) {
            if (jobInfo.id == jobId) {
                return jobInfo
            }
        }
        return null
    }
}

fun JobScheduler.isJobScheduled(jobId: Int): Boolean {
    return getPendingJobCompat(jobId) != null
}
