package me.ycdev.android.lib.commonui.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MarginItemDecorationTest {
    @Test
    fun create_appliesSameMarginToAllSides() {
        val outRect = Rect()
        val context = RuntimeEnvironment.getApplication()

        MarginItemDecoration.create(8).getItemOffsets(
            outRect,
            View(context),
            RecyclerView(context),
            RecyclerView.State()
        )

        assertThat(outRect).isEqualTo(Rect(8, 8, 8, 8))
    }

    @Test
    fun constructor_appliesIndependentMargins() {
        val outRect = Rect()
        val context = RuntimeEnvironment.getApplication()

        MarginItemDecoration(1, 2, 3, 4).getItemOffsets(
            outRect,
            View(context),
            RecyclerView(context),
            RecyclerView.State()
        )

        assertThat(outRect).isEqualTo(Rect(1, 2, 3, 4))
    }
}
