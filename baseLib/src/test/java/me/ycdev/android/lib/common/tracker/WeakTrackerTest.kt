package me.ycdev.android.lib.common.tracker

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WeakTrackerTest {
    @Test
    fun firstListenerStartsAndLastListenerStopsTracker() {
        val tracker = DemoTracker()
        val listener1 = DemoListener()
        val listener2 = DemoListener()

        tracker.addListener(listener1)
        tracker.addListener(listener2)
        tracker.removeListener(listener1)
        tracker.removeListener(listener2)

        assertThat(tracker.startCount).isEqualTo(1)
        assertThat(tracker.stopCount).isEqualTo(1)
    }

    @Test
    fun removingMiddleListenerKeepsTrackerRunning() {
        val tracker = DemoTracker()
        val listener1 = DemoListener()
        val listener2 = DemoListener()

        tracker.addListener(listener1)
        tracker.addListener(listener2)
        tracker.removeListener(listener1)

        assertThat(tracker.startCount).isEqualTo(1)
        assertThat(tracker.stopCount).isEqualTo(0)
    }

    private class DemoListener

    private class DemoTracker : WeakTracker<DemoListener>() {
        var startCount = 0
        var stopCount = 0

        override fun startTracker() {
            startCount++
        }

        override fun stopTracker() {
            stopCount++
        }
    }
}
