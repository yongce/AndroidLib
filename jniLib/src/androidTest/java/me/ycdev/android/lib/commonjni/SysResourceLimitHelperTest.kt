package me.ycdev.android.lib.commonjni

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class SysResourceLimitHelperTest {
    @Test
    fun test_getOpenFilesNumberLimit() {
        val ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
        assertNotNull("failed to get open files limit", ofLimit)
        assertTrue("current limit should be positive", ofLimit.curLimit > 0)
        assertTrue("max limit should be positive", ofLimit.maxLimit > 0)
        assertTrue("current limit should not exceed max", ofLimit.curLimit <= ofLimit.maxLimit)
        Timber.tag(TAG).d("cur limit: " + ofLimit.curLimit + ", max limit: " + ofLimit.maxLimit)
    }

    @Test
    fun test_setOpenFilesNumberLimit() {
        var ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
        assertNotNull("failed to get open files limit", ofLimit)
        val oldOfLimit = ofLimit.curLimit
        try {
            var newOfLimit = oldOfLimit * 2
            if (newOfLimit > ofLimit.maxLimit) {
                newOfLimit = ofLimit.maxLimit
            }
            var result = SysResourceLimitHelper.setOpenFilesLimit(newOfLimit)
            assertTrue("failed to set open files limit", result)
            ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
            assertNotNull("failed to get open files limit", ofLimit)
            assertEquals(
                "failed to set open files limit, double check",
                ofLimit.curLimit.toLong(),
                newOfLimit.toLong()
            )
        } finally {
            val result = SysResourceLimitHelper.setOpenFilesLimit(oldOfLimit)
            assertTrue("failed to restore open files limit", result)
            ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
            assertNotNull("failed to get open files limit", ofLimit)
            assertEquals(
                "failed to restore open files limit, double check",
                ofLimit.curLimit.toLong(),
                oldOfLimit.toLong()
            )
        }
    }

    @Test
    fun test_setOpenFilesNumberLimit_rejectsLimitAboveMaximum() {
        var ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
        assertNotNull("failed to get open files limit", ofLimit)
        val oldOfLimit = ofLimit.curLimit

        try {
            val rejected = SysResourceLimitHelper.setOpenFilesLimit(ofLimit.maxLimit + 1)
            assertFalse("limit above max should be rejected", rejected)

            ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
            assertNotNull("failed to get open files limit", ofLimit)
            assertEquals("current limit should stay unchanged", oldOfLimit.toLong(), ofLimit.curLimit.toLong())
        } finally {
            SysResourceLimitHelper.setOpenFilesLimit(oldOfLimit)
        }
    }

    companion object {
        private const val TAG = "SRLimitHelperTest"
    }
}
