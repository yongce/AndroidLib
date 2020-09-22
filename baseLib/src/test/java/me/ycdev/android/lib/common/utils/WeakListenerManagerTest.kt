package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import me.ycdev.android.lib.test.rules.TimberJvmRule
import org.junit.Rule
import org.junit.Test

class WeakListenerManagerTest {
    @get:Rule
    val timberRule = TimberJvmRule()

    @Test
    fun basic() {
        val manager = DemoListenerManager()
        val listener1 = DemoListener(manager)
        val listener2 = DemoListener(manager)

        manager.addListener(listener1)
        manager.addListener(listener2)

        assertThat(manager.listenersCount()).isEqualTo(2)

        manager.notifyListeners { l -> l.call(1) }
        assertThat(listener1.value).isEqualTo(1)
        assertThat(listener2.value).isEqualTo(1)

        manager.notifyListeners { l -> l.call(2) }
        assertThat(listener1.value).isEqualTo(2)
        assertThat(listener2.value).isEqualTo(2)

        assertThat(manager.listenersCount()).isEqualTo(2)
    }

    @Test
    fun listenerLeak() {
        val manager = DemoListenerManager()
        val listener1 = DemoListener(manager)

        manager.addListener(listener1)
        addLeakedListener(manager)

        // force GC
        GcHelper.forceGc()

        // before notify
        assertThat(manager.listenersCount()).isEqualTo(2)

        manager.notifyListeners { l -> l.call(1) }
        assertThat(listener1.value).isEqualTo(1)

        // after notify
        assertThat(manager.listenersCount()).isEqualTo(1)

        manager.notifyListeners { l -> l.call(2) }
        assertThat(listener1.value).isEqualTo(2)
    }

    @Test
    fun listenerRemovedWhenNotify() {
        val manager = DemoListenerManager()
        val listener1 = DemoListener(manager, true)
        val listener2 = DemoListener(manager)

        manager.addListener(listener1)
        manager.addListener(listener2)

        assertThat(manager.listenersCount()).isEqualTo(2)

        manager.notifyListeners { l -> l.call(1) }
        assertThat(listener1.value).isEqualTo(1)
        assertThat(listener2.value).isEqualTo(1)

        assertThat(manager.listenersCount()).isEqualTo(1)

        manager.notifyListeners { l -> l.call(2) }
        assertThat(listener1.value).isEqualTo(1)
        assertThat(listener2.value).isEqualTo(2)

        assertThat(manager.listenersCount()).isEqualTo(1)
    }

    private fun addLeakedListener(manager: DemoListenerManager) {
        manager.addListener(DemoListener(manager))
    }

    class DemoListener(
        private val manager: DemoListenerManager,
        private val notifyOnce: Boolean = false
    ) {
        var value: Int = 0

        fun call(value: Int) {
            this.value = value
            if (notifyOnce) {
                manager.removeListener(this)
            }
        }
    }

    class DemoListenerManager : WeakListenerManager<DemoListener>()
}