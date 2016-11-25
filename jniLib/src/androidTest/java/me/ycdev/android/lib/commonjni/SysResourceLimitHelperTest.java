package me.ycdev.android.lib.commonjni;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.ycdev.android.lib.common.utils.LibLogger;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SysResourceLimitHelperTest {
    private static final String TAG = "SysResourceLimitHelperTest";

    @Test
    public void test_getOpenFilesNumberLimit() {
        SysResourceLimitHelper.LimitInfo ofLimit = SysResourceLimitHelper.getOpenFilesLimit();
        assertTrue("failed to get open files limit", ofLimit != null);
        LibLogger.d(TAG, "cur limit: " + ofLimit.curLimit + ", max limit: " + ofLimit.maxLimit);
    }

    @Test
    public void test_setOpenFilesNumberLimit() {
        SysResourceLimitHelper.LimitInfo ofLimit = SysResourceLimitHelper.getOpenFilesLimit();
        assertTrue("failed to get open files limit", ofLimit != null);
        int oldOfLimit = ofLimit.curLimit;
        int newOfLimit = oldOfLimit * 2;
        if (newOfLimit > ofLimit.maxLimit) {
            newOfLimit = ofLimit.maxLimit;
        }
        boolean result = SysResourceLimitHelper.setOpenFilesLimit(newOfLimit);
        assertTrue("failed to set open files limit", result);
        ofLimit = SysResourceLimitHelper.getOpenFilesLimit();
        assertTrue("failed to get open files limit", ofLimit != null);
        assertTrue("failed to set open files limit, double check",
                ofLimit.curLimit == newOfLimit);

        result = SysResourceLimitHelper.setOpenFilesLimit(oldOfLimit);
        assertTrue("failed to restore open files limit", result);
        ofLimit = SysResourceLimitHelper.getOpenFilesLimit();
        assertTrue("failed to get open files limit", ofLimit != null);
        assertTrue("failed to restore open files limit, double check",
                ofLimit.curLimit == oldOfLimit);
    }
}
