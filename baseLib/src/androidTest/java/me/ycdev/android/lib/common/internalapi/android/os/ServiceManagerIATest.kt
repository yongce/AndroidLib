package me.ycdev.android.lib.common.internalapi.android.os

import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class ServiceManagerIATest {
    @Test
    fun test_getService() {
        assertTrue(ServiceManagerIA.checkReflectGetService())
    }

    @Test
    fun test_checkService() {
        assertTrue(ServiceManagerIA.checkReflectCheckService())
    }

    @Test
    fun test_addService() {
        assertTrue(ServiceManagerIA.checkReflectAddService())
    }

    @Test
    fun test_listServices() {
        assertTrue(ServiceManagerIA.checkReflectListServices())
    }
}
