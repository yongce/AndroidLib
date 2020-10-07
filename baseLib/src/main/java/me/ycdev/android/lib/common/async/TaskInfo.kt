package me.ycdev.android.lib.common.async

import android.os.SystemClock
import me.ycdev.android.lib.common.utils.DateTimeUtils
import java.lang.StringBuilder
import java.util.concurrent.atomic.AtomicInteger

internal class TaskInfo(val executor: ITaskExecutor, val task: Runnable, val delay: Long, val period: Long = -1) {
    private val taskId: Int = taskIdGenerator.incrementAndGet()
    var triggerAt: Long = SystemClock.elapsedRealtime() + delay

    override fun toString(): String {
        val timestamp = System.currentTimeMillis() - (SystemClock.elapsedRealtime() - triggerAt)
        return StringBuilder().append("TaskInfo[id=").append(taskId)
            .append(", delay=").append(delay)
            .append(", triggerAt=").append(DateTimeUtils.getReadableTimeStamp(timestamp))
            .append(", period=").append(period)
            .append(']')
            .toString()
    }

    companion object {
        private val taskIdGenerator = AtomicInteger(0)
    }
}