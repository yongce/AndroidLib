package me.ycdev.android.lib.test.rules

import me.ycdev.android.lib.test.log.TimberJvmTree
import org.junit.rules.ExternalResource
import timber.log.Timber

class TimberJvmRule : ExternalResource() {
    override fun before() {
        Timber.plant(TimberJvmTree())
    }
}
