package me.ycdev.android.lib.common.internalapi.android.os

import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test

@SmallTest
class ProcessIATest {
    @Test
    fun test_parseCmdlineProcessName() {
        assertEquals(
            "me.ycdev.android.lib.common.test",
            ProcessIA.parseCmdlineProcessName(
                "me.ycdev.android.lib.common.test\u0000--nice-name=ignored"
            )
        )
    }
}
