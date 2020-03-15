package me.ycdev.android.lib.commonui.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val marginLeft: Int,
    private val marginTop: Int,
    private val marginRight: Int,
    private val marginBottom: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            left = marginLeft
            top = marginTop
            right = marginRight
            bottom = marginBottom
        }
    }

    companion object {
        fun create(margin: Int): MarginItemDecoration {
            return MarginItemDecoration(margin, margin, margin, margin)
        }
    }
}
