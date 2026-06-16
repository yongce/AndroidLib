package me.ycdev.android.lib.commonjni

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class FileStatusHelperTest {
    @Test
    fun test_getFileStatus() {
        val targetContext = ApplicationProvider.getApplicationContext<Context>()
        val targetUid = targetContext.applicationInfo.uid
        val testFile = targetContext.filesDir
        val fileStatus =
            FileStatusHelper.getFileStatus(
                testFile.absolutePath
            )
        assertNotNull(fileStatus)
        Timber.tag(TAG).i(
            "uid: " + fileStatus!!.uid + ", gid: " + fileStatus.gid +
                ", mode: " + Integer.toOctalString(fileStatus.mode)
        )
        assertEquals("check uid", targetUid.toLong(), fileStatus.uid.toLong())
        assertEquals("check gid", targetUid.toLong(), fileStatus.gid.toLong())
    }

    @Test
    fun test_getFileStatus_missingFile() {
        val targetContext = ApplicationProvider.getApplicationContext<Context>()
        val missingFile = targetContext.filesDir.resolve("missing-file-for-status-test")

        assertNull(FileStatusHelper.getFileStatus(missingFile.absolutePath))
    }

    companion object {
        private const val TAG = "FileStatusHelperTest"
    }
}
