package me.ycdev.android.lib.commonui.base

import java.util.Collections
import java.util.Comparator

import android.app.Activity
import android.content.Context
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class ListAdapterBase<ItemType, VH : ViewHolderBase>(protected val context: Context) :
    BaseAdapter() {
    protected var inflater: LayoutInflater = if (context is Activity) {
        context.layoutInflater
    } else {
        LayoutInflater.from(context)
    }
    protected var list: List<ItemType>? = null

    @get:LayoutRes
    protected abstract val itemLayoutResId: Int

    /**
     * @return null will be returned if no data set.
     */
    fun getData(): List<ItemType>? {
        return list
    }

    open fun setData(data: List<ItemType>?) {
        list = data
        notifyDataSetChanged()
    }

    fun sort(comparator: Comparator<ItemType>) {
        Collections.sort(list!!, comparator)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (list != null) list!!.size else 0
    }

    override fun getItem(position: Int): ItemType {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val holder: VH
        if (itemView == null) {
            itemView = inflater.inflate(itemLayoutResId, parent, false)
            holder = createViewHolder(itemView!!, position)
            itemView.tag = holder
        } else {
            @Suppress("UNCHECKED_CAST")
            val tmp = itemView.tag as VH
            holder = tmp
        }
        bindView(getItem(position), holder)
        return itemView
    }

    protected abstract fun createViewHolder(itemView: View, position: Int): VH
    protected abstract fun bindView(item: ItemType, holder: VH)
}
