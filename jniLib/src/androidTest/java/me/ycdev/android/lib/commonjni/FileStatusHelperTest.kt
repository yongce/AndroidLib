package me.ycdev.android.lib.commonjni

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
        assertTrue("check mode", fileStatus.mode > 0)
    }

    @Test
    fun test_getFileStatus_missingFile() {
        val targetContext = ApplicationProvider.getApplicationContext<Context>()
        val missingFile = targetContext.filesDir.resolve("missing-file-for-status-test")

        assertNull(FileStatusHelper.getFileStatus(missingFile.absolutePath))
    }

    @Test
    fun test_getFileStatus_emptyPath() {
        assertNull(FileStatusHelper.getFileStatus(""))
    }

    @Test
    fun test_getFileStatus_regularFileAndDirectory() {
        val targetContext = ApplicationProvider.getApplicationContext<Context>()
        val directory = targetContext.filesDir.resolve("file-status-test-dir")
        val regularFile = directory.resolve("regular-file.txt")
        directory.mkdirs()
        regularFile.writeText("file status")

        val directoryStatus = FileStatusHelper.getFileStatus(directory.absolutePath)
        val fileStatus = FileStatusHelper.getFileStatus(regularFile.absolutePath)

        assertNotNull(directoryStatus)
        assertNotNull(fileStatus)
        assertTrue("directory should be executable", directory.canExecute())
        assertTrue("regular file should exist", regularFile.isFile)
        assertFalse("regular file should not be directory", regularFile.isDirectory)
        assertTrue("directory mode should be set", directoryStatus!!.mode > 0)
        assertTrue("file mode should be set", fileStatus!!.mode > 0)
    }

    companion object {
        private const val TAG = "FileStatusHelperTest"
    }
}
