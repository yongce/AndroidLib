package me.ycdev.android.lib.common.internalapi.android.os

import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class UserHandleIATest {
    @Test
    fun test_myUserId() {
        assertTrue(UserHandleIA.checkReflect_myUserId())
    }
}
