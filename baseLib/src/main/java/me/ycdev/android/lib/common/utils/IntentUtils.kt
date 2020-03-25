@file:Suppress("unused")

package me.ycdev.android.lib.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.PowerManager
import androidx.annotation.IntDef
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
object IntentUtils {
    private const val TAG = "IntentUtils"

    const val INTENT_TYPE_ACTIVITY = 1
    const val INTENT_TYPE_BROADCAST = 2
    const val INTENT_TYPE_SERVICE = 3

    @IntDef(INTENT_TYPE_ACTIVITY, INTENT_TYPE_BROADCAST, INTENT_TYPE_SERVICE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IntentType

    const val EXTRA_FOREGROUND_SERVICE = "extra.foreground_service"

    fun canStartActivity(cxt: Context, intent: Intent): Boolean {
        // Use PackageManager.MATCH_DEFAULT_ONLY to behavior same as Context#startAcitivty()
        val resolveInfo = cxt.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfo != null
    }

    fun startActivity(context: Context, intent: Intent): Boolean {
        return if (canStartActivity(context, intent)) {
            context.startActivity(intent)
            true
        } else {
            Timber.tag(TAG).w("cannot start Activity: $intent")
            false
        }
    }

    fun needForegroundService(
        context: Context,
        ai: ApplicationInfo,
        listenSensor: Boolean
    ): Boolean {
        // no background limitation before Android O
        if (VERSION.SDK_INT < VERSION_CODES.O) {
            return false
        }

        // Need foreground service on Android P to listen sensors
        if (listenSensor && VERSION.SDK_INT >= VERSION_CODES.P) {
            return true
        }

        // The background limitation only works when the targetSdk is 26 or higher
        if (ai.targetSdkVersion < VERSION_CODES.O) {
            return false
        }

        // no background limitation if the app is in the battery optimization whitelist.
        val powerMgr = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerMgr.isIgnoringBatteryOptimizations(ai.packageName)) {
            return false
        }

        // yes, we need foreground service
        return true
    }

    fun needForegroundService(context: Context, listenSensor: Boolean): Boolean {
        return needForegroundService(context, context.applicationInfo, listenSensor)
    }

    @SuppressLint("NewApi")
    fun startService(context: Context, intent: Intent): Boolean {
        val resolveInfo = context.packageManager.resolveService(intent, 0) ?: return false
        intent.setClassName(
            resolveInfo.serviceInfo.packageName,
            resolveInfo.serviceInfo.name
        )
        // Here, we should set "listenSensor" to false.
        // The target service still can set "listenSensor" to true for its checking.
        if (needForegroundService(context, resolveInfo.serviceInfo.applicationInfo, false)) {
            intent.putExtra(EXTRA_FOREGROUND_SERVICE, true)
            context.startForegroundService(intent)
        } else {
            intent.putExtra(EXTRA_FOREGROUND_SERVICE, false)
            context.startService(intent)
        }
        return true
    }

    fun startForegroundService(context: Context, intent: Intent): Boolean {
        val resolveInfo = context.packageManager.resolveService(intent, 0) ?: return false
        intent.setClassName(
            resolveInfo.serviceInfo.packageName,
            resolveInfo.serviceInfo.name
        )
        if (VERSION.SDK_INT < VERSION_CODES.O) {
            intent.putExtra(EXTRA_FOREGROUND_SERVICE, false)
            context.startService(intent)
        } else {
            // here, we add an extra so that the target service can know
            // it needs to call #startForeground()
            intent.putExtra(EXTRA_FOREGROUND_SERVICE, true)
            context.startForegroundService(intent)
        }
        return true
    }
}
