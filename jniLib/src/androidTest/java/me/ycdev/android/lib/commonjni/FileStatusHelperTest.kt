package me.ycdev.android.lib.commonjni

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.ycdev.android.lib.common.utils.LibLogger
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FileStatusHelperTest {

    @Test
    fun test_getFileStatus() {
        val targetContext = ApplicationProvider.getApplicationContext<Context>()
        val targetUid = targetContext.applicationInfo.uid
        val testFile = targetContext.filesDir
        val fileStatus = FileStatusHelper.getFileStatus(
            testFile.absolutePath
        )
        LibLogger.i(
            TAG, "uid: " + fileStatus.uid + ", gid: " + fileStatus.gid +
                    ", mode: " + Integer.toOctalString(fileStatus.mode)
        )
        assertEquals("check uid", targetUid.toLong(), fileStatus.uid.toLong())
        assertEquals("check gid", targetUid.toLong(), fileStatus.gid.toLong())
    }

    companion object {
        private const val TAG = "FileStatusHelperTest"
    }
}
