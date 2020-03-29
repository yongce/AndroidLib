package me.ycdev.android.lib.common.tracker

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import me.ycdev.android.lib.common.utils.LibLogger
import me.ycdev.android.lib.common.wrapper.BroadcastHelper

/**
 * A tracker to track the interactive state of the device.
 */
@Suppress("unused")
class InteractiveStateTracker private constructor(cxt: Context) :
    WeakTracker<InteractiveStateTracker.InteractiveStateListener>() {

    private val appContext: Context = cxt.applicationContext
    private var interactive: Boolean = false
    private var needRefreshInteractiveState: Boolean = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            LibLogger.i(TAG, "Received: $action")
            if (Intent.ACTION_USER_PRESENT == action) {
                notifyUserPresent()
            } else {
                interactive = Intent.ACTION_SCREEN_ON == action
                notifyInteractiveChanged(interactive)
            }
        }
    }

    val isInteractive: Boolean
        get() {
            if (needRefreshInteractiveState) {
                refreshInteractiveState()
            }
            return interactive
        }

    interface InteractiveStateListener {
        /**
         * Will be invoked when Intent.ACTION_SCREEN_ON or Intent.ACTION_SCREEN_OFF received.
         */
        fun onInteractiveChanged(interactive: Boolean)

        /**
         * Will be invoked when Intent.ACTION_USER_PRESENT received.
         */
        fun onUserPresent()
    }

    private fun refreshInteractiveState() {
        val pm = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        interactive = pm.isInteractive
    }

    override fun startTracker() {
        LibLogger.i(TAG, "Screen on/off tracker is running")
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        BroadcastHelper.registerForExternal(appContext, receiver, filter)

        refreshInteractiveState()
        needRefreshInteractiveState = false
    }

    override fun stopTracker() {
        LibLogger.i(TAG, "Screen on/off tracker is stopped")
        appContext.unregisterReceiver(receiver)
        needRefreshInteractiveState = true
    }

    override fun onListenerAdded(listener: InteractiveStateListener) {
        listener.onInteractiveChanged(interactive)
    }

    private fun notifyInteractiveChanged(interactive: Boolean) {
        notifyListeners { it.onInteractiveChanged(interactive) }
    }

    private fun notifyUserPresent() {
        notifyListeners { it.onUserPresent() }
    }

    companion object {
        private const val TAG = "InteractiveStateTracker"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: InteractiveStateTracker? = null

        fun getInstance(cxt: Context): InteractiveStateTracker {
            if (instance == null) {
                synchronized(InteractiveStateTracker::class.java) {
                    if (instance == null) {
                        instance = InteractiveStateTracker(cxt)
                    }
                }
            }
            return instance!!
        }
    }
}
