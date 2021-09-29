package me.ycdev.android.lib.common.utils

import androidx.test.filters.SmallTest
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@SmallTest
class ReflectionUtilsTest {
    private open class TestA {
        var a1: String? = null
        var a2: Int = 0
        private val a3: String? = null
        private val a4: Long = 0

        open fun a9(): String {
            return "a9"
        }

        protected open fun a10(a: Int): Int {
            return a
        }

        private fun a11(a: String, b: Long): String {
            return a + b
        }

        companion object {

            var a5: String? = null
            var a6: Int = 0
            private var a7: String? = null
            private var a8: Long = 0

            @JvmStatic
            fun a13(): String {
                return "a13"
            }

            @JvmStatic
            protected fun a14(a: Long): Long {
                return a
            }

            @JvmStatic
            private fun a15(a: Int, b: String): String {
                return b + a
            }
        }
    }

    private class TestB : TestA() {
        var b1: String? = null
        var b2: Int = 0
        private val b3: String? = null
        private val b4: Long = 0

        override fun a9(): String {
            return "a9"
        }

        override fun a10(a: Int): Int {
            return a
        }

        private fun b11(a: String, b: Long): String {
            return a + b
        }

        companion object {

            var b5: String? = null
            var b6: Int = 0
            private var b7: String? = null
            private var b8: Long = 0

            @JvmStatic
            fun b13(): String {
                return "b13"
            }

            @JvmStatic
            protected fun b14(a: Long): Long {
                return a
            }

            @JvmStatic
            private fun b15(a: Int, b: String): String {
                return b + a
            }
        }
    }

    @Test
    fun test_findMethod() {
        try {
            val objB = TestB()

            // TestA part
            val a11 = ReflectionUtils.findMethod(
                TestB::class.java,
                "a11",
                String::class.java,
                Long::class.java
            )
            assertTrue(a11.declaringClass == TestA::class.java && a11.name == "a11")
            assertTrue(a11.invoke(objB, "a", 11L) == "a11")

            val a13 = ReflectionUtils.findMethod(TestB::class.java, "a13")
            assertTrue(a13.declaringClass == TestA::class.java && a13.name == "a13")
            assertTrue(a13.invoke(null) == "a13")

            val a14 =
                ReflectionUtils.findMethod(TestB::class.java, "a14", Long::class.java)
            assertTrue(a14.declaringClass == TestA::class.java && a14.name == "a14")
            assertTrue(a14.invoke(null, 14L) as Long == 14L)

            // TODO fix the following case
//            val a15 = ReflectionUtils.findMethod(
//                TestB::class.java,
//                "a15",
//                Int::class.java,
//                String::class.java
//            )
//            assertTrue(a15.declaringClass == TestA::class.java && a15.name == "a15")
//            assertTrue(a15.invoke(null, 15, "a") == "a15")

            // TestB part
            val a9 = ReflectionUtils.findMethod(TestB::class.java, "a9")
            assertTrue(a9.declaringClass == TestB::class.java && a9.name == "a9")
            assertTrue(a9.invoke(objB) == "a9")

            val a10 =
                ReflectionUtils.findMethod(TestB::class.java, "a10", Int::class.java)
            assertTrue(a10.declaringClass == TestB::class.java && a10.name == "a10")
            assertTrue(a10.invoke(objB, 10) as Int == 10)

            val b11 = ReflectionUtils.findMethod(
                TestB::class.java,
                "b11",
                String::class.java,
                Long::class.java
            )
            assertTrue(b11.declaringClass == TestB::class.java && b11.name == "b11")
            assertTrue(b11.invoke(objB, "b", 11L) == "b11")

            val b13 = ReflectionUtils.findMethod(TestB::class.java, "b13")
            assertTrue(b13.declaringClass == TestB::class.java && b13.name == "b13")
            assertTrue(b13.invoke(null) == "b13")

            val b14 =
                ReflectionUtils.findMethod(TestB::class.java, "b14", Long::class.java)
            assertTrue(b14.declaringClass == TestB::class.java && b14.name == "b14")
            assertTrue(b14.invoke(null, 14L) as Long == 14L)

            // TODO fix the following case
//            val b15 = ReflectionUtils.findMethod(
//                TestB::class.java,
//                "b15",
//                Int::class.java,
//                String::class.java
//            )
//            assertTrue(b15.declaringClass == TestB::class.java && b15.name == "b15")
//            assertTrue(b15.invoke(null, 15, "b") == "b15")
        } catch (e: Exception) {
            e.printStackTrace()
            fail("failed to reflect: " + e.toString())
        }
    }

    @Test
    fun test_findField() {
        try {
            val objB = TestB()

            // TestA part
            val a1 = ReflectionUtils.findField(TestB::class.java, "a1")
            assertTrue(a1.declaringClass == TestA::class.java && a1.name == "a1")
            a1.set(objB, "a1")
            assertTrue(a1.get(objB) == "a1")

            val a2 = ReflectionUtils.findField(TestB::class.java, "a2")
            assertTrue(a2.declaringClass == TestA::class.java && a2.name == "a2")
            a2.set(objB, 2)
            assertTrue(a2.get(objB) as Int == 2)

            val a3 = ReflectionUtils.findField(TestB::class.java, "a3")
            assertTrue(a3.declaringClass == TestA::class.java && a3.name == "a3")
            a3.set(objB, "a3")
            assertTrue(a3.get(objB) == "a3")

            val a4 = ReflectionUtils.findField(TestB::class.java, "a4")
            assertTrue(a4.declaringClass == TestA::class.java && a4.name == "a4")
            a4.set(objB, 4L)
            assertTrue(a4.get(objB) as Long == 4L)

            val a5 = ReflectionUtils.findField(TestB::class.java, "a5")
            assertTrue(a5.declaringClass == TestA::class.java && a5.name == "a5")
            a5.set(null, "a5")
            assertTrue(a5.get(null) == "a5")

            val a6 = ReflectionUtils.findField(TestB::class.java, "a6")
            assertTrue(a6.declaringClass == TestA::class.java && a6.name == "a6")
            a6.set(null, 6)
            assertTrue(a6.get(null) as Int == 6)

            val a7 = ReflectionUtils.findField(TestB::class.java, "a7")
            assertTrue(a7.declaringClass == TestA::class.java && a7.name == "a7")
            a7.set(null, "a7")
            assertTrue(a7.get(null) == "a7")

            val a8 = ReflectionUtils.findField(TestB::class.java, "a8")
            assertTrue(a8.declaringClass == TestA::class.java && a8.name == "a8")
            a8.set(null, 8L)
            assertTrue(a8.get(null) as Long == 8L)

            // TestB part
            val b1 = ReflectionUtils.findField(TestB::class.java, "b1")
            assertTrue(b1.declaringClass == TestB::class.java && b1.name == "b1")
            a1.set(objB, "b1")
            assertTrue(a1.get(objB) == "b1")

            val b2 = ReflectionUtils.findField(TestB::class.java, "b2")
            assertTrue(b2.declaringClass == TestB::class.java && b2.name == "b2")
            b2.set(objB, 2)
            assertTrue(b2.get(objB) as Int == 2)

            val b3 = ReflectionUtils.findField(TestB::class.java, "b3")
            assertTrue(b3.declaringClass == TestB::class.java && b3.name == "b3")
            b3.set(objB, "b3")
            assertTrue(b3.get(objB) == "b3")

            val b4 = ReflectionUtils.findField(TestB::class.java, "b4")
            assertTrue(b4.declaringClass == TestB::class.java && b4.name == "b4")
            b4.set(objB, 4L)
            assertTrue(b4.get(objB) as Long == 4L)

            val b5 = ReflectionUtils.findField(TestB::class.java, "b5")
            assertTrue(b5.declaringClass == TestB::class.java && b5.name == "b5")
            b5.set(null, "b5")
            assertTrue(b5.get(null) == "b5")

            val b6 = ReflectionUtils.findField(TestB::class.java, "b6")
            assertTrue(b6.declaringClass == TestB::class.java && b6.name == "b6")
            b6.set(null, 6)
            assertTrue(b6.get(null) as Int == 6)

            val b7 = ReflectionUtils.findField(TestB::class.java, "b7")
            assertTrue(b7.declaringClass == TestB::class.java && b7.name == "b7")
            b7.set(null, "b7")
            assertTrue(b7.get(null) == "b7")

            val b8 = ReflectionUtils.findField(TestB::class.java, "b8")
            assertTrue(b8.declaringClass == TestB::class.java && b8.name == "b8")
            b8.set(null, 8L)
            assertTrue(b8.get(null) as Long == 8L)
        } catch (e: Exception) {
            e.printStackTrace()
            fail("failed to reflect: " + e.toString())
        }
    }
}
