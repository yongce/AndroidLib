package me.ycdev.android.lib.commonui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import me.ycdev.android.lib.commonui.R
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GridEntriesActivityTest {
    @After
    fun tearDown() {
        TestGridEntriesActivity.entries = emptyList()
        AsyncGridEntriesActivity.entries = emptyList()
        AsyncGridEntriesActivity.loadStarted = CountDownLatch(1)
        AsyncGridEntriesActivity.allowLoadReturn = CountDownLatch(1)
    }

    @Test
    fun loadItems_emptyListHidesLoadingAndLeavesAdapterEmpty() {
        TestGridEntriesActivity.entries = emptyList()

        val activity = buildActivity()

        assertThat(activity.loadingVisibility()).isEqualTo(View.GONE)
        assertThat(activity.adapterItemCount()).isEqualTo(0)
    }

    @Test
    fun loadItems_populatedListUpdatesAdapter() {
        TestGridEntriesActivity.entries =
            listOf(
                GridEntriesActivity.Entry("One", "First"),
                GridEntriesActivity.Entry("Two", "Second")
            )

        val activity = buildActivity()

        assertThat(activity.loadingVisibility()).isEqualTo(View.GONE)
        assertThat(activity.adapterItemCount()).isEqualTo(2)
    }

    @Test
    fun itemClick_dispatchesEntryClickAction() {
        val clickCount = AtomicInteger(0)
        TestGridEntriesActivity.entries =
            listOf(
                GridEntriesActivity.Entry(
                    title = "Clickable",
                    desc = "Click action",
                    clickAction = { clickCount.incrementAndGet() }
                )
            )
        val activity = buildActivity()
        val holder = activity.createAndBindHolder(position = 0)

        holder.itemView.performClick()

        assertThat(clickCount.get()).isEqualTo(1)
    }

    @Test
    fun itemLongClick_dispatchesEntryLongClickAction() {
        val longClickCount = AtomicInteger(0)
        TestGridEntriesActivity.entries =
            listOf(
                GridEntriesActivity.Entry(
                    title = "LongClickable",
                    desc = "Long click action",
                    longClickAction = { longClickCount.incrementAndGet() }
                )
            )
        val activity = buildActivity()
        val holder = activity.createAndBindHolder(position = 0)

        assertThat(holder.itemView.performLongClick()).isTrue()

        assertThat(longClickCount.get()).isEqualTo(1)
    }

    @Test
    fun loadItems_asyncShowsLoadingUntilEntriesAreLoaded() {
        AsyncGridEntriesActivity.entries =
            listOf(
                GridEntriesActivity.Entry("Async One", "First"),
                GridEntriesActivity.Entry("Async Two", "Second")
            )
        val controller = Robolectric.buildActivity(AsyncGridEntriesActivity::class.java).setup()
        val activity = controller.get()

        assertThat(AsyncGridEntriesActivity.loadStarted.await(3, TimeUnit.SECONDS)).isTrue()
        assertThat(activity.loadingVisibility()).isEqualTo(View.VISIBLE)
        assertThat(activity.adapterItemCount()).isEqualTo(0)

        AsyncGridEntriesActivity.allowLoadReturn.countDown()

        assertThat(waitUntil { activity.adapterItemCount() == 2 }).isTrue()
        assertThat(activity.loadingVisibility()).isEqualTo(View.GONE)
    }

    private fun buildActivity(): TestGridEntriesActivity = Robolectric.buildActivity(TestGridEntriesActivity::class.java).setup().get()

    private fun waitUntil(condition: () -> Boolean): Boolean {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3)
        while (System.nanoTime() < deadline) {
            shadowOf(android.os.Looper.getMainLooper()).idle()
            if (condition()) {
                return true
            }
            Thread.yield()
        }
        return condition()
    }

    class TestGridEntriesActivity : GridEntriesActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(R.style.YcdevTheme_Light)
            super.onCreate(savedInstanceState)
        }

        override fun loadIntents(): List<Entry> = entries

        fun loadingVisibility(): Int = loadingView.visibility

        fun adapterItemCount(): Int = entriesAdapter.itemCount

        @Suppress("UNCHECKED_CAST")
        fun createAndBindHolder(position: Int): RecyclerView.ViewHolder {
            val adapter = entriesAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
            val holder = adapter.onCreateViewHolder(gridView, adapter.getItemViewType(position))
            adapter.onBindViewHolder(holder, position)
            return holder
        }

        companion object {
            var entries: List<Entry> = emptyList()
        }
    }

    class AsyncGridEntriesActivity : GridEntriesActivity() {
        override val needLoadIntentsAsync: Boolean = true

        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(R.style.YcdevTheme_Light)
            super.onCreate(savedInstanceState)
        }

        override fun loadIntents(): List<Entry> {
            loadStarted.countDown()
            assertThat(allowLoadReturn.await(3, TimeUnit.SECONDS)).isTrue()
            return entries
        }

        fun loadingVisibility(): Int = loadingView.visibility

        fun adapterItemCount(): Int = entriesAdapter.itemCount

        companion object {
            var entries: List<Entry> = emptyList()
            var loadStarted = CountDownLatch(1)
            var allowLoadReturn = CountDownLatch(1)
        }
    }
}
