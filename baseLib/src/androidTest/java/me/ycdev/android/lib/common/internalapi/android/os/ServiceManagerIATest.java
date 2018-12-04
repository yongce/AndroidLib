package me.ycdev.android.lib.common.internalapi.android.os;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ServiceManagerIATest {
    @Test
    public void test_getService() {
        assertTrue(ServiceManagerIA.checkReflect_getService());
    }

    @Test
    public void test_checkService() {
        assertTrue(ServiceManagerIA.checkReflect_checkService());
    }

    @Test
    public void test_addService() {
        assertTrue(ServiceManagerIA.checkReflect_addService());
    }

    @Test
    public void test_listServices() {
        assertTrue(ServiceManagerIA.checkReflect_listServices());
    }
}
