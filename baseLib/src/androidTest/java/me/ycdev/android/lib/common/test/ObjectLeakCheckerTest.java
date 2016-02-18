package me.ycdev.android.lib.common.test;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ObjectLeakCheckerTest {
    private static class Dummy {}

    private static Dummy sHolder;

    @Test
    public void testNoLeak() {
        ObjectLeakChecker.ObjectOperator<Dummy> operator = new ObjectLeakChecker.ObjectOperator<Dummy>() {
            @Override
            public Dummy createObject() {
                return new Dummy();
            }

            @Override
            public void operate(Dummy obj) {
                // nothing to do
            }
        };
        ObjectLeakChecker<Dummy> checker = new ObjectLeakChecker<>(operator);
        checker.prepareForGc();
        checker.waitGcDone();
        assertEquals(0, checker.getLeakedObjectCount());
    }

    @Test
    public void testLeak() {
        ObjectLeakChecker.ObjectOperator<Dummy> operator = new ObjectLeakChecker.ObjectOperator<Dummy>() {
            @Override
            public Dummy createObject() {
                return new Dummy();
            }

            @Override
            public void operate(Dummy obj) {
                sHolder = obj;
            }
        };
        ObjectLeakChecker<Dummy> checker = new ObjectLeakChecker<>(operator);
        checker.prepareForGc();
        checker.waitGcDone();
        assertEquals(1, checker.getLeakedObjectCount());
    }
}
