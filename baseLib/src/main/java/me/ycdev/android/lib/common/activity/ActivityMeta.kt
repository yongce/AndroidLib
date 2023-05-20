package me.ycdev.android.lib.common.activity

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.annotation.VisibleForTesting
import java.util.concurrent.ConcurrentHashMap

data class ActivityMeta(
    val componentName: ComponentName,
    val taskAffinity: String,
    val launchMode: Int,
    val allowTaskReparenting: Boolean
) {
    companion object {
        private val cache = ConcurrentHashMap<String, ActivityMeta>()

        /**
         * @throws PackageManager.NameNotFoundException if component not found in the system
         */
        fun get(context: Context, activity: ComponentName): ActivityMeta {
            val key = activity.flattenToShortString()
            var meta = cache[key]
            if (meta != null) {
                return meta
            }

            @Suppress("DEPRECATION")
            val info = context.packageManager.getActivityInfo(activity, 0)
            val taskAffinity = info.taskAffinity ?: context.applicationInfo.taskAffinity
            val allowTaskReparenting = (info.flags and ActivityInfo.FLAG_ALLOW_TASK_REPARENTING) > 0
            meta = ActivityMeta(activity, taskAffinity, info.launchMode, allowTaskReparenting)
            cache[key] = meta
            return meta
        }

        @VisibleForTesting
        internal fun initCache(vararg metas: ActivityMeta) {
            cache.clear()
            metas.forEach {
                val key = it.componentName.flattenToShortString()
                cache[key] = it
            }
        }
    }
}
