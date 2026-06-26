package me.ycdev.android.arch.activity

import android.os.Bundle
import androidx.appcompat.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AppCompatBaseActivityTest {
    @Test
    fun appCompatBaseActivity_launchesThroughCreateStartResume() {
        val activity = Robolectric.buildActivity(TestAppCompatBaseActivity::class.java).setup().get()

        assertThat(activity.isFinishing).isFalse()
    }

    @Test
    fun appCompatBaseActivity_canSkipHomeAsUpSetup() {
        val activity = Robolectric.buildActivity(NoHomeAsUpActivity::class.java).setup().get()

        assertThat(activity.isFinishing).isFalse()
    }

    open class TestAppCompatBaseActivity : AppCompatBaseActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar)
            super.onCreate(savedInstanceState)
        }
    }

    class NoHomeAsUpActivity : TestAppCompatBaseActivity() {
        override fun shouldSetDisplayHomeAsUpEnabled(): Boolean = false
    }
}
