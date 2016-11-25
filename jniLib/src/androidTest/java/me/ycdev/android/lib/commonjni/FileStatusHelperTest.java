package me.ycdev.android.lib.commonjni;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import me.ycdev.android.lib.common.utils.LibLogger;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FileStatusHelperTest {
    private static final String TAG = "FileStatusHelperTest";

    @Test
    public void test_getFileStatus() {
        Context targetContext = InstrumentationRegistry.getContext();
        int targetUid = targetContext.getApplicationInfo().uid;
        File testFile = targetContext.getFilesDir();
        FileStatusHelper.FileStatus fileStatus = FileStatusHelper.getFileStatus(
                testFile.getAbsolutePath());
        LibLogger.i(TAG, "uid: " + fileStatus.uid + ", gid: " + fileStatus.gid
                + ", mode: " + Integer.toOctalString(fileStatus.mode));
        assertEquals("check uid", targetUid, fileStatus.uid);
        assertEquals("check gid", targetUid, fileStatus.gid);
    }
}
