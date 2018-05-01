package me.ycdev.android.lib.common.utils;

import org.junit.Test;

import me.ycdev.android.lib.common.type.BooleanHolder;
import me.ycdev.android.lib.test.base.NormalJUnitBase;
import timber.log.Timber;

public class GcHelperTest extends NormalJUnitBase {
    private static final String TAG = "GcHelperTest";

    @Test
    public void forceGc_default() {
        GcHelper.forceGc();
        // GC happened
    }

    @Test
    public void forceGc_holder() {
        BooleanHolder gcState = new BooleanHolder(false);
        {
            new Object() {
                @Override
                protected void finalize() throws Throwable {
                    Timber.tag(TAG).d("forceGc_holder, GC Partner object was collected");
                    gcState.value = true;
                }
            };
        }
        GcHelper.forceGc(gcState);
    }
}
