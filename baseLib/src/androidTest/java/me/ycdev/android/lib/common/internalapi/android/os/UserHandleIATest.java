package me.ycdev.android.lib.common.internalapi.android.os;

import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.ycdev.android.lib.common.utils.LibLogger;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserHandleIATest {
    private static final String TAG = "UserHandleIATest";

    @Before
    public void setUp() throws Exception {
        LibLogger.i(TAG, "setup");
    }

    @After
    public void tearDown() throws Exception {
        LibLogger.i(TAG, "tearDown");
    }

    @Test
    public void test_myUserId() {
        assertTrue(UserHandleIA.checkReflect_myUserId());
    }

}
