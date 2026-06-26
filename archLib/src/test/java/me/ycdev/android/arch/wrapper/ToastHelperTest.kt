package me.ycdev.android.arch.wrapper

import android.widget.Toast
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ToastHelperTest {
    @Test
    fun show_displaysTextToast() {
        val context = RuntimeEnvironment.getApplication()

        ToastHelper.show(context, "hello", Toast.LENGTH_SHORT)

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("hello")
    }
}
