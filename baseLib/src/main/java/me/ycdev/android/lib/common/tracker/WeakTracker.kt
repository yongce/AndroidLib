package me.ycdev.android.lib.common.tracker

import me.ycdev.android.lib.common.manager.ListenerManager

abstract class WeakTracker<IListener : Any> : ListenerManager<IListener>(true) {
    protected abstract fun startTracker()
    protected abstract fun stopTracker()

    override fun onFirstListenerAdd() {
        startTracker()
    }

    override fun onLastListenerRemoved() {
        stopTracker()
    }
}
