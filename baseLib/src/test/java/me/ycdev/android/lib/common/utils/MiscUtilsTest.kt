package me.ycdev.android.lib.common.utils

import androidx.test.filters.SmallTest

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals

@SmallTest
class MiscUtilsTest {

    @Before
    @Throws(Exception::class)
    fun setUp() {
        LibLogger.enableJvmLogger()
        LibLogger.i(TAG, "setup")
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        LibLogger.i(TAG, "tearDown")
    }

    @Test
    fun test_calcProgressPercent() {
        for (i in 1..100) {
            assertEquals(i.toLong(), MiscUtils.calcProgressPercent(1, 100, i, 100).toLong())
        }

        for (i in 1..50) {
            assertEquals((i * 2).toLong(), MiscUtils.calcProgressPercent(1, 100, i, 50).toLong())
        }

        for (i in 1..100) {
            assertEquals(
                ((i + 1) / 2).toLong(),
                MiscUtils.calcProgressPercent(1, 100, i, 200).toLong()
            )
        }

        // special cases
        assertEquals(2, MiscUtils.calcProgressPercent(1, 100, 1, 57).toLong())
        assertEquals(4, MiscUtils.calcProgressPercent(1, 100, 2, 57).toLong())
        assertEquals(6, MiscUtils.calcProgressPercent(1, 100, 3, 57).toLong())
        assertEquals(7, MiscUtils.calcProgressPercent(1, 100, 4, 57).toLong())
        assertEquals(9, MiscUtils.calcProgressPercent(1, 100, 5, 57).toLong())
        // ...
        assertEquals(93, MiscUtils.calcProgressPercent(1, 100, 53, 57).toLong())
        assertEquals(94, MiscUtils.calcProgressPercent(1, 100, 54, 57).toLong())
        assertEquals(96, MiscUtils.calcProgressPercent(1, 100, 55, 57).toLong())
        assertEquals(98, MiscUtils.calcProgressPercent(1, 100, 56, 57).toLong())
        assertEquals(100, MiscUtils.calcProgressPercent(1, 100, 57, 57).toLong())

        // special cases
        assertEquals(1, MiscUtils.calcProgressPercent(1, 100, 1, 157).toLong())
        assertEquals(2, MiscUtils.calcProgressPercent(1, 100, 2, 157).toLong())
        assertEquals(2, MiscUtils.calcProgressPercent(1, 100, 3, 157).toLong())
        assertEquals(3, MiscUtils.calcProgressPercent(1, 100, 4, 157).toLong())
        assertEquals(4, MiscUtils.calcProgressPercent(1, 100, 5, 157).toLong())
        assertEquals(4, MiscUtils.calcProgressPercent(1, 100, 6, 157).toLong())
        // ...
        assertEquals(97, MiscUtils.calcProgressPercent(1, 100, 153, 157).toLong())
        assertEquals(98, MiscUtils.calcProgressPercent(1, 100, 154, 157).toLong())
        assertEquals(98, MiscUtils.calcProgressPercent(1, 100, 155, 157).toLong())
        assertEquals(99, MiscUtils.calcProgressPercent(1, 100, 156, 157).toLong())
        assertEquals(100, MiscUtils.calcProgressPercent(1, 100, 157, 157).toLong())
    }

    companion object {
        private const val TAG = "MiscUtilsTest"
    }
}
