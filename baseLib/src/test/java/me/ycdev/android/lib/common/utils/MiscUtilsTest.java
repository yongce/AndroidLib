package me.ycdev.android.lib.common.utils;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SmallTest
public class MiscUtilsTest {
    private static final String TAG = "MiscUtilsTest";

    @Before
    public void setUp() throws Exception {
        LibLogger.enableJvmLogger();
        LibLogger.i(TAG, "setup");
    }

    @After
    public void tearDown() throws Exception {
        LibLogger.i(TAG, "tearDown");
    }

    @Test
    public void test_calcProgressPercent() {
        for (int i = 1; i <= 100; i++) {
            assertEquals(i, MiscUtils.calcProgressPercent(1, 100, i, 100));
        }

        for (int i = 1; i <= 50; i++) {
            assertEquals(i * 2, MiscUtils.calcProgressPercent(1, 100, i, 50));
        }

        for (int i = 1; i <= 100; i++) {
            assertEquals((i + 1) / 2, MiscUtils.calcProgressPercent(1, 100, i, 200));
        }

        // special cases
        assertEquals(2, MiscUtils.calcProgressPercent(1, 100, 1, 57));
        assertEquals(4, MiscUtils.calcProgressPercent(1, 100, 2, 57));
        assertEquals(6, MiscUtils.calcProgressPercent(1, 100, 3, 57));
        assertEquals(7, MiscUtils.calcProgressPercent(1, 100, 4, 57));
        assertEquals(9, MiscUtils.calcProgressPercent(1, 100, 5, 57));
        // ...
        assertEquals(93, MiscUtils.calcProgressPercent(1, 100, 53, 57));
        assertEquals(94, MiscUtils.calcProgressPercent(1, 100, 54, 57));
        assertEquals(96, MiscUtils.calcProgressPercent(1, 100, 55, 57));
        assertEquals(98, MiscUtils.calcProgressPercent(1, 100, 56, 57));
        assertEquals(100, MiscUtils.calcProgressPercent(1, 100, 57, 57));

        // special cases
        assertEquals(1, MiscUtils.calcProgressPercent(1, 100, 1, 157));
        assertEquals(2, MiscUtils.calcProgressPercent(1, 100, 2, 157));
        assertEquals(2, MiscUtils.calcProgressPercent(1, 100, 3, 157));
        assertEquals(3, MiscUtils.calcProgressPercent(1, 100, 4, 157));
        assertEquals(4, MiscUtils.calcProgressPercent(1, 100, 5, 157));
        assertEquals(4, MiscUtils.calcProgressPercent(1, 100, 6, 157));
        // ...
        assertEquals(97, MiscUtils.calcProgressPercent(1, 100, 153, 157));
        assertEquals(98, MiscUtils.calcProgressPercent(1, 100, 154, 157));
        assertEquals(98, MiscUtils.calcProgressPercent(1, 100, 155, 157));
        assertEquals(99, MiscUtils.calcProgressPercent(1, 100, 156, 157));
        assertEquals(100, MiscUtils.calcProgressPercent(1, 100, 157, 157));
    }
}
