package me.ycdev.android.lib.common.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InteractiveStateTrackerTest {
    @Test
    fun receiverUpdatesInteractiveStateForScreenBroadcasts() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tracker = newTracker(context)
        val receiver = tracker.receiver()
        val states = mutableListOf<Boolean>()
        val listener =
            object : InteractiveStateTracker.InteractiveStateListener {
                override fun onInteractiveChanged(interactive: Boolean) {
                    states.add(interactive)
                }

                override fun onUserPresent() = Unit
            }

        tracker.addListener(listener)
        try {
            states.clear()

            receiver.onReceive(context, Intent(Intent.ACTION_SCREEN_OFF))
            receiver.onReceive(context, Intent(Intent.ACTION_SCREEN_ON))

            assertThat(states).containsExactly(false, true).inOrder()
            assertThat(tracker.isInteractive).isTrue()
        } finally {
            tracker.removeListener(listener)
        }
    }

    @Test
    fun receiverNotifiesUserPresent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tracker = newTracker(context)
        val receiver = tracker.receiver()
        var userPresentCount = 0
        val listener =
            object : InteractiveStateTracker.InteractiveStateListener {
                override fun onInteractiveChanged(interactive: Boolean) = Unit

                override fun onUserPresent() {
                    userPresentCount++
                }
            }

        tracker.addListener(listener)
        try {
            receiver.onReceive(context, Intent(Intent.ACTION_USER_PRESENT))

            assertThat(userPresentCount).isEqualTo(1)
        } finally {
            tracker.removeListener(listener)
        }
    }

    private fun newTracker(context: Context): InteractiveStateTracker {
        val constructor = InteractiveStateTracker::class.java.getDeclaredConstructor(Context::class.java)
        constructor.isAccessible = true
        return constructor.newInstance(context)
    }

    private fun InteractiveStateTracker.receiver(): BroadcastReceiver {
        val field = InteractiveStateTracker::class.java.getDeclaredField("receiver")
        field.isAccessible = true
        return field.get(this) as BroadcastReceiver
    }
}
