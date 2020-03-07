package me.ycdev.android.lib.commonui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import me.ycdev.android.arch.ArchConstants
import me.ycdev.android.arch.ArchConstants.IntentType
import me.ycdev.android.arch.activity.AppCompatBaseActivity
import me.ycdev.android.arch.wrapper.ToastHelper
import me.ycdev.android.lib.common.utils.IntentUtils
import me.ycdev.android.lib.common.wrapper.BroadcastHelper
import me.ycdev.android.lib.commonui.R
import me.ycdev.android.lib.commonui.base.ListAdapterBase
import me.ycdev.android.lib.commonui.base.ViewHolderBase

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class GridEntriesActivity : AppCompatBaseActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {

    protected lateinit var adapter: SystemEntriesAdapter
    protected lateinit var gridView: GridView

    protected open val contentViewLayout: Int
        @LayoutRes get() = R.layout.commonui_grid_entries

    protected abstract val intents: List<Entry>

    open class Entry(
        open val title: CharSequence,
        open val desc: CharSequence,
        open val clickAction: ((Context) -> Unit)? = null,
        open val longClickAction: ((Context) -> Unit)? = null
    )

    open class IntentEntry(
        @IntentType val type: Int = ArchConstants.INTENT_TYPE_ACTIVITY,
        val intent: Intent,
        title: String,
        desc: String,
        val perm: String? = null
    ) : Entry(title, desc) {
        constructor(intent: Intent, title: String, desc: String) :
                this(ArchConstants.INTENT_TYPE_ACTIVITY, intent, title, desc)

        override val clickAction: ((Context) -> Unit)? = ::onItemClicked
        override val longClickAction: ((Context) -> Unit)? = ::onItemLongClicked

        fun onItemClicked(context: Context) {
            if (type == ArchConstants.INTENT_TYPE_ACTIVITY) {
                IntentUtils.startActivity(context, intent)
            } else if (type == ArchConstants.INTENT_TYPE_BROADCAST) {
                BroadcastHelper.sendToExternal(context, intent, perm)
            }
        }

        private fun onItemLongClicked(context: Context) {
            ToastHelper.show(context, desc, Toast.LENGTH_LONG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewLayout)

        adapter = SystemEntriesAdapter(this)

        gridView = findViewById(R.id.grid)
        gridView.adapter = adapter
        gridView.onItemClickListener = this
        gridView.onItemLongClickListener = this

        loadItems()
    }

    @SuppressLint("StaticFieldLeak")
    private fun loadItems() {
        if (needLoadIntentsAsync) {
            object : AsyncTask<Void, Void, List<Entry>>() {
                override fun doInBackground(vararg params: Void): List<Entry> {
                    return intents
                }

                override fun onPostExecute(result: List<Entry>) {
                    adapter.setData(intents)
                }
            }.execute()
        } else {
            adapter.setData(intents)
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = adapter.getItem(position)
        item.clickAction?.invoke(this)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        val item = adapter.getItem(position)
        item.longClickAction?.invoke(this)
        return true
    }

    /**
     * Decide if we need to invoke [.getIntent] async.
     * @return true for async and false for sync. false by default
     */
    protected open val needLoadIntentsAsync: Boolean = false

    protected open class SystemEntriesAdapter(cxt: Context) :
        ListAdapterBase<Entry, SystemEntriesAdapter.ViewHolder>(cxt) {

        override val itemLayoutResId: Int = R.layout.commonui_grid_entries_item

        override fun createViewHolder(itemView: View, position: Int): ViewHolder {
            return ViewHolder(itemView, position)
        }

        override fun bindView(item: Entry, holder: ViewHolder) {
            holder.titleView.text = item.title
        }

        class ViewHolder(itemView: View, position: Int) :
            ViewHolderBase(itemView, position) {
            var titleView: TextView = itemView.findViewById(R.id.title)
        }
    }
}
