package me.ycdev.android.lib.test

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObjectLeakCheckerTest {
    private class Dummy

    @Test
    fun testNoLeak() {
        val operator = object : ObjectLeakChecker.ObjectOperator<Dummy> {
            override fun createObject(): Dummy {
                return Dummy()
            }

            override fun operate(obj: Dummy) {
                // nothing to do
            }
        }
        val checker = ObjectLeakChecker(operator)
        checker.prepareForGc()
        checker.waitGcDone()
        assertThat(checker.leakedObjectCount).isEqualTo(0)
    }

    @Test
    fun testLeak() {
        val operator = object : ObjectLeakChecker.ObjectOperator<Dummy> {
            override fun createObject(): Dummy {
                return Dummy()
            }

            override fun operate(obj: Dummy) {
                holder = obj
            }
        }
        val checker = ObjectLeakChecker(operator)
        checker.prepareForGc()
        checker.waitGcDone()
        assertThat(checker.leakedObjectCount).isEqualTo(1)
    }

    companion object {

        private var holder: Dummy? = null
    }
}
