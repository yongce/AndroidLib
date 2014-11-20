package me.ycdev.android.lib.common.internalapi.android.os;

import android.os.Build;
import android.test.AndroidTestCase;

import me.ycdev.android.lib.common.utils.Logger;

public class SystemPropertiesIATest extends AndroidTestCase {
    private static final String TAG = "SystemPropertiesIATest";

    private static final String TEST_KEY_NONE = "test.internalapis.none";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.i(TAG, "setup");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Logger.i(TAG, "tearDown");
    }

    public void test_get() {
        String defValue = "test.defValue";
        String actual = SystemPropertiesIA.get(TEST_KEY_NONE, defValue);
        assertEquals(defValue, actual);

        actual = SystemPropertiesIA.get("ro.product.model", defValue);
        assertEquals(Build.MODEL, actual);

        actual = SystemPropertiesIA.get("ro.build.fingerprint", defValue);
        assertEquals(Build.FINGERPRINT, actual);
    }

    public void test_getInt() {
        int defValue = 123;
        int actual = SystemPropertiesIA.getInt(TEST_KEY_NONE, defValue);
        assertEquals(defValue, actual);

        actual = SystemPropertiesIA.getInt("ro.build.version.sdk", defValue);
        assertEquals(Build.VERSION.SDK_INT, actual);
    }

    public void test_getLong() {
        long defValue = 123;
        long actual = SystemPropertiesIA.getLong(TEST_KEY_NONE, defValue);
        assertEquals(defValue, actual);

        // Build.getLong("ro.build.date.utc") * 1000
        actual = SystemPropertiesIA.getLong("ro.build.date.utc", defValue) * 1000;
        assertEquals(Build.TIME, actual);
    }

    public void test_getBoolean() {
        boolean defValue = true;
        boolean actual = SystemPropertiesIA.getBoolean(TEST_KEY_NONE, defValue);
        assertEquals(defValue, actual);

        actual = SystemPropertiesIA.getBoolean("ro.com.android.dataroaming", true);
        boolean actual2 = SystemPropertiesIA.getBoolean("ro.com.android.dataroaming", false);
        assertEquals(actual, actual2);
    }
}
