package me.ycdev.android.lib.common.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.common.truth.Truth.assertThat
import java.lang.ref.WeakReference
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class WeakHandlerTest {
    @Test
    fun handleMessage_dispatchesToLiveCallback() {
        val callback = RecordingCallback()
        val handler = WeakHandler(Looper.getMainLooper(), callback)

        handler.sendEmptyMessage(42)
        shadowOf(Looper.getMainLooper()).idle()

        assertThat(callback.messages).containsExactly(42)
    }

    @Test
    fun handleMessage_ignoresReleasedCallback() {
        val callback = RecordingCallback()
        val handler = WeakHandler(Looper.getMainLooper(), callback)
        clearTargetReference(handler)

        handler.sendEmptyMessage(42)
        shadowOf(Looper.getMainLooper()).idle()

        assertThat(callback.messages).isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    private fun clearTargetReference(handler: WeakHandler) {
        val field = WeakHandler::class.java.getDeclaredField("targetHandler")
        field.isAccessible = true
        val reference = field.get(handler) as WeakReference<Handler.Callback>
        reference.clear()
    }

    private class RecordingCallback : Handler.Callback {
        val messages = mutableListOf<Int>()

        override fun handleMessage(msg: Message): Boolean {
            messages.add(msg.what)
            return true
        }
    }
}
