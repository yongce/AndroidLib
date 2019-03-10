package me.ycdev.android.lib.commonjni

import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class SysResourceLimitHelperTest {

    @Test
    fun test_getOpenFilesNumberLimit() {
        val ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
        assertNotNull("failed to get open files limit", ofLimit)
        Timber.tag(TAG).d("cur limit: " + ofLimit.curLimit + ", max limit: " + ofLimit.maxLimit)
    }

    @Test
    fun test_setOpenFilesNumberLimit() {
        var ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
        assertNotNull("failed to get open files limit", ofLimit)
        val oldOfLimit = ofLimit.curLimit
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

        result = SysResourceLimitHelper.setOpenFilesLimit(oldOfLimit)
        assertTrue("failed to restore open files limit", result)
        ofLimit = SysResourceLimitHelper.getOpenFilesLimit()
        assertNotNull("failed to get open files limit", ofLimit)
        assertEquals(
            "failed to restore open files limit, double check",
            ofLimit.curLimit.toLong(),
            oldOfLimit.toLong()
        )
    }

    companion object {
        private const val TAG = "SRLimitHelperTest"
    }
}
