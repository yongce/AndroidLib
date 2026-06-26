package me.ycdev.android.arch.activity

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BaseActivityTest {
    @Test
    fun baseActivity_launchesThroughCreateStartResume() {
        val activity = Robolectric.buildActivity(TestBaseActivity::class.java).setup().get()

        assertThat(activity.isFinishing).isFalse()
    }

    class TestBaseActivity : BaseActivity()
}
