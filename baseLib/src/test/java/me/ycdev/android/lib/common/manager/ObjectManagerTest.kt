package me.ycdev.android.lib.common.manager

import com.google.common.truth.Truth.assertThat
import me.ycdev.android.lib.common.utils.GcHelper
import me.ycdev.android.lib.test.rules.TimberJvmRule
import org.junit.Rule
import org.junit.Test

class ObjectManagerTest {
    @get:Rule
    val timberRule = TimberJvmRule()

    @Test
    fun basic() {
        val managersList = arrayListOf<ObjectManager<DemoObject>>(
            ObjectManager(true),
            ObjectManager(false)
        )
        for (manager in managersList) {
            val obj1 = DemoObject(manager)
            val obj2 = DemoObject(manager)

            manager.addObject(obj1)
            manager.addObject(obj2)

            assertThat(manager.objectsCount).isEqualTo(2)

            manager.notifyObjects { l -> l.call(1) }
            assertThat(obj1.value).isEqualTo(1)
            assertThat(obj2.value).isEqualTo(1)

            manager.notifyObjects { l -> l.call(2) }
            assertThat(obj1.value).isEqualTo(2)
            assertThat(obj2.value).isEqualTo(2)

            assertThat(manager.objectsCount).isEqualTo(2)
        }
    }

    @Test
    fun objectLeak() {
        val managersList = arrayListOf<ObjectManager<DemoObject>>(
            ObjectManager(true),
            ObjectManager(false)
        )
        for (manager in managersList) {
            val obj1 = DemoObject(manager)

            manager.addObject(obj1)
            addLeakedObject(manager)

            // force GC
            GcHelper.forceGc()

            // before notify
            assertThat(manager.objectsCount).isEqualTo(2)

            manager.notifyObjects { l -> l.call(1) }
            assertThat(obj1.value).isEqualTo(1)

            // after notify
            if (manager.weakReference) {
                // the object collected by GC was also removed by ObjectManager!
                assertThat(manager.objectsCount).isEqualTo(1)
            } else {
                assertThat(manager.objectsCount).isEqualTo(2)
            }

            manager.notifyObjects { l -> l.call(2) }
            assertThat(obj1.value).isEqualTo(2)
        }
    }

    @Test
    fun objectRemovedWhenNotify() {
        val managersList = arrayListOf<ObjectManager<DemoObject>>(
            ObjectManager(true),
            ObjectManager(false)
        )
        for (manager in managersList) {
            val obj1 = DemoObject(manager, true)
            val obj2 = DemoObject(manager)

            manager.addObject(obj1)
            manager.addObject(obj2)

            assertThat(manager.objectsCount).isEqualTo(2)

            manager.notifyObjects { l -> l.call(1) }
            assertThat(obj1.value).isEqualTo(1)
            assertThat(obj2.value).isEqualTo(1)

            assertThat(manager.objectsCount).isEqualTo(1)

            manager.notifyObjects { l -> l.call(2) }
            assertThat(obj1.value).isEqualTo(1)
            assertThat(obj2.value).isEqualTo(2)

            assertThat(manager.objectsCount).isEqualTo(1)
        }
    }

    private fun addLeakedObject(manager: ObjectManager<DemoObject>) {
        manager.addObject(DemoObject(manager))
    }

    class DemoObject(
        private val manager: ObjectManager<DemoObject>,
        private val notifyOnce: Boolean = false
    ) {
        var value: Int = 0

        fun call(value: Int) {
            this.value = value
            if (notifyOnce) {
                manager.removeObject(this)
            }
        }
    }
}
