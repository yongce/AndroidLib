package me.ycdev.android.lib.common.activity

import android.content.ComponentName
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityInfoTest {
    private val testComponent = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz")

    @Test
    fun makeCopy() {
        val origin = ActivityInfo(testComponent, 10, ActivityInfo.State.Started)
        val copied = origin.makeCopy()
        assertThat(copied.componentName).isEqualTo(testComponent)
        assertThat(copied.taskId).isEqualTo(10)
        assertThat(copied.state).isEqualTo(ActivityInfo.State.Started)
    }
}
