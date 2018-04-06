package me.ycdev.android.lib.test.base;

import org.junit.BeforeClass;

import me.ycdev.android.lib.test.log.TimberJvmTree;
import timber.log.Timber;

public class NormalJUnitBase {
    @BeforeClass
    public static void setupClass() {
        Timber.plant(new TimberJvmTree());
    }
}
