package me.ycdev.android.lib.common.utils;

import android.test.AndroidTestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class ReflectionUtilsTest extends AndroidTestCase {
    private static class TestA {
        public String a1;
        public int a2;
        private String a3;
        private long a4;

        public static String a5;
        public static int a6;
        private static String a7;
        private static long a8;

        public String a9() {
            return "a9";
        }

        protected int a10(int a) {
            return a;
        }

        private String a11(String a, long b) {
            return a + b;
        }

        public static String a13() {
            return "a13";
        }

        protected static long a14(long a) {
            return a;
        }

        private static String a15(int a, String b) {
            return b + a;
        }
    }

    private static class TestB extends TestA {
        public String b1;
        public int b2;
        private String b3;
        private long b4;

        public static String b5;
        public static int b6;
        private static String b7;
        private static long b8;

        @Override
        public String a9() {
            return "a9";
        }

        @Override
        protected int a10(int a) {
            return a;
        }

        private String b11(String a, long b) {
            return a + b;
        }

        public static String b13() {
            return "b13";
        }

        protected static long b14(long a) {
            return a;
        }

        private static String b15(int a, String b) {
            return b + a;
        }
    }

    public void test_findMethod() {
        try {
            TestB objB = new TestB();

            // TestA part
            Method a11 = ReflectionUtils.findMethod(TestB.class, "a11", String.class, long.class);
            assertTrue(a11.getDeclaringClass() == TestA.class && a11.getName().equals("a11"));
            assertTrue(a11.invoke(objB, "a", 11l).equals("a11"));

            Method a13 = ReflectionUtils.findMethod(TestB.class, "a13");
            assertTrue(a13.getDeclaringClass() == TestA.class && a13.getName().equals("a13"));
            assertTrue(a13.invoke(null).equals("a13"));

            Method a14 = ReflectionUtils.findMethod(TestB.class, "a14", long.class);
            assertTrue(a14.getDeclaringClass() == TestA.class && a14.getName().equals("a14"));
            assertTrue((long) a14.invoke(null, 14l) == 14l);

            Method a15 = ReflectionUtils.findMethod(TestB.class, "a15", int.class, String.class);
            assertTrue(a15.getDeclaringClass() == TestA.class && a15.getName().equals("a15"));
            assertTrue(a15.invoke(null, 15, "a").equals("a15"));

            // TestB part
            Method a9 = ReflectionUtils.findMethod(TestB.class, "a9");
            assertTrue(a9.getDeclaringClass() == TestB.class && a9.getName().equals("a9"));
            assertTrue(a9.invoke(objB).equals("a9"));

            Method a10 = ReflectionUtils.findMethod(TestB.class, "a10", int.class);
            assertTrue(a10.getDeclaringClass() == TestB.class && a10.getName().equals("a10"));
            assertTrue((int) a10.invoke(objB, 10) == 10);

            Method b11 = ReflectionUtils.findMethod(TestB.class, "b11", String.class, long.class);
            assertTrue(b11.getDeclaringClass() == TestB.class && b11.getName().equals("b11"));
            assertTrue(b11.invoke(objB, "b", 11l).equals("b11"));

            Method b13 = ReflectionUtils.findMethod(TestB.class, "b13");
            assertTrue(b13.getDeclaringClass() == TestB.class && b13.getName().equals("b13"));
            assertTrue(b13.invoke(null).equals("b13"));

            Method b14 = ReflectionUtils.findMethod(TestB.class, "b14", long.class);
            assertTrue(b14.getDeclaringClass() == TestB.class && b14.getName().equals("b14"));
            assertTrue((long) b14.invoke(null, 14l) == 14l);

            Method b15 = ReflectionUtils.findMethod(TestB.class, "b15", int.class, String.class);
            assertTrue(b15.getDeclaringClass() == TestB.class && b15.getName().equals("b15"));
            assertTrue(b15.invoke(null, 15, "b").equals("b15"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to reflect: " + e.toString());
        }
    }

    public void test_findField() {
        try {
            TestB objB = new TestB();

            // TestA part
            Field a1 = ReflectionUtils.findField(TestB.class, "a1");
            assertTrue(a1.getDeclaringClass() == TestA.class && a1.getName().equals("a1"));
            a1.set(objB, "a1");
            assertTrue(a1.get(objB).equals("a1"));

            Field a2 = ReflectionUtils.findField(TestB.class, "a2");
            assertTrue(a2.getDeclaringClass() == TestA.class && a2.getName().equals("a2"));
            a2.set(objB, 2);
            assertTrue((int) a2.get(objB) == 2);

            Field a3 = ReflectionUtils.findField(TestB.class, "a3");
            assertTrue(a3.getDeclaringClass() == TestA.class && a3.getName().equals("a3"));
            a3.set(objB, "a3");
            assertTrue(a3.get(objB).equals("a3"));

            Field a4 = ReflectionUtils.findField(TestB.class, "a4");
            assertTrue(a4.getDeclaringClass() == TestA.class && a4.getName().equals("a4"));
            a4.set(objB, 4l);
            assertTrue((long) a4.get(objB) == 4l);

            Field a5 = ReflectionUtils.findField(TestB.class, "a5");
            assertTrue(a5.getDeclaringClass() == TestA.class && a5.getName().equals("a5"));
            a5.set(null, "a5");
            assertTrue(a5.get(null).equals("a5"));

            Field a6 = ReflectionUtils.findField(TestB.class, "a6");
            assertTrue(a6.getDeclaringClass() == TestA.class && a6.getName().equals("a6"));
            a6.set(null, 6);
            assertTrue((int) a6.get(null) == 6);

            Field a7 = ReflectionUtils.findField(TestB.class, "a7");
            assertTrue(a7.getDeclaringClass() == TestA.class && a7.getName().equals("a7"));
            a7.set(null, "a7");
            assertTrue(a7.get(null).equals("a7"));

            Field a8 = ReflectionUtils.findField(TestB.class, "a8");
            assertTrue(a8.getDeclaringClass() == TestA.class && a8.getName().equals("a8"));
            a8.set(null, 8l);
            assertTrue((long) a8.get(null) == 8l);

            // TestB part
            Field b1 = ReflectionUtils.findField(TestB.class, "b1");
            assertTrue(b1.getDeclaringClass() == TestB.class && b1.getName().equals("b1"));
            a1.set(objB, "b1");
            assertTrue(a1.get(objB).equals("b1"));

            Field b2 = ReflectionUtils.findField(TestB.class, "b2");
            assertTrue(b2.getDeclaringClass() == TestB.class && b2.getName().equals("b2"));
            b2.set(objB, 2);
            assertTrue((int) b2.get(objB) == 2);

            Field b3 = ReflectionUtils.findField(TestB.class, "b3");
            assertTrue(b3.getDeclaringClass() == TestB.class && b3.getName().equals("b3"));
            b3.set(objB, "b3");
            assertTrue(b3.get(objB).equals("b3"));

            Field b4 = ReflectionUtils.findField(TestB.class, "b4");
            assertTrue(b4.getDeclaringClass() == TestB.class && b4.getName().equals("b4"));
            b4.set(objB, 4l);
            assertTrue((long) b4.get(objB) == 4l);

            Field b5 = ReflectionUtils.findField(TestB.class, "b5");
            assertTrue(b5.getDeclaringClass() == TestB.class && b5.getName().equals("b5"));
            b5.set(null, "b5");
            assertTrue(b5.get(null).equals("b5"));

            Field b6 = ReflectionUtils.findField(TestB.class, "b6");
            assertTrue(b6.getDeclaringClass() == TestB.class && b6.getName().equals("b6"));
            b6.set(null, 6);
            assertTrue((int) b6.get(null) == 6);

            Field b7 = ReflectionUtils.findField(TestB.class, "b7");
            assertTrue(b7.getDeclaringClass() == TestB.class && b7.getName().equals("b7"));
            b7.set(null, "b7");
            assertTrue(b7.get(null).equals("b7"));

            Field b8 = ReflectionUtils.findField(TestB.class, "b8");
            assertTrue(b8.getDeclaringClass() == TestB.class && b8.getName().equals("b8"));
            b8.set(null, 8l);
            assertTrue((long) b8.get(null) == 8l);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to reflect: " + e.toString());
        }
    }
}

