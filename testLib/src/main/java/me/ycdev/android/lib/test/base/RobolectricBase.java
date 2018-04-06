package me.ycdev.android.lib.test.base;

import org.junit.BeforeClass;
import org.robolectric.shadows.ShadowLog;

public class RobolectricBase {
    @BeforeClass
    public static void setupClass() {
        ShadowLog.stream = System.out;
    }
}
