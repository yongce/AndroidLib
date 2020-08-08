package me.ycdev.android.lib.common.activity

import android.content.ComponentName
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityRunningStateTest {
    private val testComponent = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz")

    @Test
    fun makeCopy() {
        val origin = ActivityRunningState(testComponent, 0xa0001, 10, ActivityRunningState.State.Started)
        val copied = origin.makeCopy()
        assertThat(copied.componentName).isEqualTo(testComponent)
        assertThat(copied.hashCode).isEqualTo(0xa0001)
        assertThat(copied.taskId).isEqualTo(10)
        assertThat(copied.state).isEqualTo(ActivityRunningState.State.Started)
    }
}
