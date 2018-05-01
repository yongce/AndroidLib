package me.ycdev.android.lib.common.utils;

import me.ycdev.android.lib.common.type.BooleanHolder;
import timber.log.Timber;

public class GcHelper {
    private static final String TAG = "GcHelper";

    public static void forceGc(BooleanHolder gcState) {
        // Now, 'objPartner' can be collected by GC!
        final long timeStart = System.currentTimeMillis();

        // create a lot of objects to force GC
        final int MEM_ALLOC_SIZE = 1024 * 1024; // 1MB
        long memAllocCount = 0;
        while (true) {
            System.gc();
            ThreadUtils.sleep(100); // wait for GC
            if (gcState.value) {
                break; // GC happened
            }
            Timber.tag(TAG).d("Allocating mem...");
            @SuppressWarnings("unused")
            byte[] gcObj = new byte[MEM_ALLOC_SIZE];
            memAllocCount++;
        }

        long timeUsed = System.currentTimeMillis() - timeStart;
        Timber.tag(TAG).d("Force GC, time used: %d, memAlloc: %dMB", timeUsed, memAllocCount);
    }

    public static void forceGc() {
        BooleanHolder gcState = new BooleanHolder(false);
        // Must use another method to create the GC object. Don't know why!
        createGcObject(gcState);
        forceGc(gcState);
    }

    private static void createGcObject(BooleanHolder gcState) {
        @SuppressWarnings("unused")
        Object objPartner = new Object() {
            @Override
            protected void finalize() throws Throwable {
                Timber.tag(TAG).d("GC Partner object was collected");
                gcState.value = true;
            }
        };
    }
}
