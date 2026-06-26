package me.ycdev.android.lib.commonui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.atomic.AtomicInteger
import me.ycdev.android.lib.commonui.R
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GridEntriesActivityTest {
    @After
    fun tearDown() {
        TestGridEntriesActivity.entries = emptyList()
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

    private fun buildActivity(): TestGridEntriesActivity = Robolectric.buildActivity(TestGridEntriesActivity::class.java).setup().get()

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
}
