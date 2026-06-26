package me.ycdev.android.lib.test.log

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import timber.log.Timber

class TimberJvmTreeTest {
    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun plantIfNeeded_plantsOnlyOneJvmTree() {
        TimberJvmTree.plantIfNeeded()
        TimberJvmTree.plantIfNeeded()

        assertThat(Timber.forest().filterIsInstance<TimberJvmTree>()).hasSize(1)
    }

    @Test
    fun keepLogsRecordsAndClearRemovesLogs() {
        val tree = TimberJvmTree()
        tree.keepLogs()
        Timber.plant(tree)

        Timber.tag("Tag").d("message")
        assertThat(tree.hasLogs()).isTrue()

        tree.clear()
        assertThat(tree.hasLogs()).isFalse()
    }
}
