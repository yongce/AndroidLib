package me.ycdev.android.lib.common.tracker

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

import me.ycdev.android.lib.common.utils.LibLogger
import me.ycdev.android.lib.common.wrapper.BroadcastHelper
import me.ycdev.android.lib.common.wrapper.IntentHelper

@Suppress("unused")
class BatteryInfoTracker private constructor(cxt: Context) :
    WeakTracker<BatteryInfoTracker.BatteryInfoListener>() {

    private val context: Context = cxt.applicationContext
    private var batteryInfo: BatteryInfo? = null
    private var batteryScale = 100

    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            LibLogger.i(TAG, "Received: ${intent.action}")
            updateBatteryInfo(intent)
        }
    }

    data class BatteryInfo(
        var level: Int = 0,
        var scale: Int = 0,
        var percent: Int = 0, // percent corrected by us
        var status: Int = BatteryManager.BATTERY_STATUS_UNKNOWN,
        var plugged: Int = 0,
        var voltage: Int = 0,
        var temperature: Double = 0.0
    )

    interface BatteryInfoListener {
        /**
         * @param newData Read-only, cannot be modified.
         */
        fun onBatteryInfoUpdated(newData: BatteryInfo)
    }

    override fun startTracker() {
        LibLogger.i(TAG, "BatteryInfo tracker is running")
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        val intent = BroadcastHelper.registerForExternal(context, batteryInfoReceiver, filter)
        if (intent != null) {
            updateBatteryInfo(intent)
        }
    }

    override fun stopTracker() {
        LibLogger.i(TAG, "BatteryInfo tracker is stopped")
        context.unregisterReceiver(batteryInfoReceiver)
    }

    override fun onListenerAdded(listener: BatteryInfoListener) {
        batteryInfo?.let { listener.onBatteryInfoUpdated(it) }
    }

    private fun updateBatteryInfo(intent: Intent) {
        val data = BatteryInfo()
        data.level = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_LEVEL, 0)
        data.scale = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_SCALE, 100)
        data.status = IntentHelper.getIntExtra(
            intent,
            BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN
        )
        data.plugged = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_PLUGGED, 0)
        data.voltage = IntentHelper.getIntExtra(intent, BatteryManager.EXTRA_VOLTAGE, 0)
        data.temperature = IntentHelper.getIntExtra(
            intent, BatteryManager.EXTRA_TEMPERATURE, 0
        ) * 0.1

        fixData(data)

        val reportedPercent = if (data.scale < 1) data.level else data.level * 100 / data.scale
        data.percent = when {
            reportedPercent < 0 -> 0
            reportedPercent > 100 -> 100
            else -> reportedPercent
        }

        LibLogger.d(TAG, "battery info updated: $data")
        batteryInfo = data
        notifyListeners { it.onBatteryInfoUpdated(data) }
    }

    private fun fixData(data: BatteryInfo) {
        // We may need to update 'batteryScale'
        if (data.level > data.scale) {
            LibLogger.e(
                TAG, "Bad battery data! level: %d, scale: %d, batteryScale: %d",
                data.level, data.scale, batteryScale
            )
            if (data.level % 100 == 0) {
                batteryScale = data.level
            }
        }

        // We may need to correct the 'data.scale'
        if (data.scale < batteryScale) {
            data.scale = batteryScale
        }
    }

    companion object {
        private const val TAG = "BatteryInfoTracker"

        @SuppressLint("StaticFieldLeak")
        private var instance: BatteryInfoTracker? = null

        @Synchronized
        fun getInstance(cxt: Context): BatteryInfoTracker {
            if (instance == null) {
                synchronized(BatteryInfoTracker::class.java) {
                    if (instance == null) {
                        instance = BatteryInfoTracker(cxt)
                    }
                }
            }
            return instance!!
        }
    }
}
