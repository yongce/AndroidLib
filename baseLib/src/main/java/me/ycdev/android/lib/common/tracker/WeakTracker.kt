package me.ycdev.android.lib.common.tracker

import me.ycdev.android.lib.common.utils.WeakListenerManager

abstract class WeakTracker<IListener : Any> : WeakListenerManager<IListener>() {
    protected abstract fun startTracker()
    protected abstract fun stopTracker()

    override fun onFirstListenerAdd() {
        startTracker()
    }

    override fun onLastListenerRemoved() {
        stopTracker()
    }
}
