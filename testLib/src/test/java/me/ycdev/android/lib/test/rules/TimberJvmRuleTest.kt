package me.ycdev.android.lib.test.rules

import com.google.common.truth.Truth.assertThat
import me.ycdev.android.lib.test.log.TimberJvmTree
import org.junit.After
import org.junit.Rule
import org.junit.Test
import timber.log.Timber

class TimberJvmRuleTest {
    @Rule @JvmField
    val timberJvmRule = TimberJvmRule()

    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun beforePlantsJvmTree() {
        assertThat(Timber.forest().filterIsInstance<TimberJvmTree>()).hasSize(1)
    }
}
