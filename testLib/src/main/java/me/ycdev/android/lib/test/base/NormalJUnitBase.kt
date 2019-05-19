package me.ycdev.android.lib.test.base

import me.ycdev.android.lib.test.log.TimberJvmTree
import org.junit.BeforeClass
import timber.log.Timber

open class NormalJUnitBase {
    companion object {
        @BeforeClass @JvmStatic
        fun setupClass() {
            Timber.plant(TimberJvmTree())
        }
    }
}
