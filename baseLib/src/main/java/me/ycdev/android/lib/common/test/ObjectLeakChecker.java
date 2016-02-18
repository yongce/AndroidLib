package me.ycdev.android.lib.common.test;

import android.os.SystemClock;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

public class ObjectLeakChecker<T> {
    public interface ObjectOperator<T> {
        T createObject();
        void operate(T obj);
    }

    private CountDownLatch mLatch = new CountDownLatch(2);
    private ObjectOperator<T> mTargetOperator;
    private WeakReference<T> mTargetObjHolder;

    private boolean mIsPrepared = false;
    private boolean mIsGcDone = false;

    public ObjectLeakChecker(ObjectOperator<T> operator) {
        mTargetOperator = operator;
    }

    public void prepareForGc() {
        Object objPartner = new Object() {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                mLatch.countDown();
            }
        };

        T targetObj = mTargetOperator.createObject();
        mTargetObjHolder = new WeakReference<T>(targetObj);
        mTargetOperator.operate(targetObj);
        mIsPrepared = true;
    }

    public void waitGcDone() {
        if (!mIsPrepared) {
            throw new RuntimeException("please call #prepareForGc() first");
        }

        // create a lot of objects to force GC
        while (true) {
            byte[] gcObj = new byte[1024 * 1024]; // 1MB
            SystemClock.sleep(50); // wait for GC
            if (mLatch.getCount() < 2) {
                break; // GC happened
            }
        }
        if (mTargetObjHolder.get() == null) {
            mLatch.countDown();
        }
        mIsGcDone = true;
    }

    public long getLeakedObjectCount() {
        if (!mIsGcDone) {
            throw new RuntimeException("please call #waitGcDone() first");
        }
        return mLatch.getCount();
    }
}
