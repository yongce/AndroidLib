package me.ycdev.android.lib.test.rules

import me.ycdev.android.lib.test.log.TimberJvmTree
import org.junit.rules.ExternalResource

class TimberJvmRule : ExternalResource() {
    override fun before() {
        TimberJvmTree.plantIfNeeded()
    }
}
