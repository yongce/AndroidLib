package me.ycdev.android.lib.test.base

import org.junit.BeforeClass
import org.robolectric.shadows.ShadowLog

@Suppress("unused")
open class RobolectricBase {
    companion object {
        @BeforeClass @JvmStatic
        fun setupClass() {
            ShadowLog.stream = System.out
        }
    }
}
