package me.ycdev.android.lib.common.internalapi.android.os

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserHandleIATest {
    @Test
    fun test_myUserId() {
        assertTrue(UserHandleIA.checkReflectMyUserId())
    }
}
