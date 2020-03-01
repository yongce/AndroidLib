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
import me.ycdev.android.arch.activity.AppCompatBaseActivity
import me.ycdev.android.arch.wrapper.ToastHelper
import me.ycdev.android.lib.common.utils.IntentUtils
import me.ycdev.android.lib.commonui.R
import me.ycdev.android.lib.commonui.base.ListAdapterBase
import me.ycdev.android.lib.commonui.base.ViewHolderBase

import me.ycdev.android.arch.ArchConstants.IntentType

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class GridEntriesActivity : AppCompatBaseActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {

    protected lateinit var adapter: SystemEntriesAdapter
    protected lateinit var gridView: GridView

    protected open val contentViewLayout: Int
        @LayoutRes get() = R.layout.commonui_grid_entries

    protected abstract val intents: List<IntentEntry>

    class IntentEntry(var intent: Intent, var title: String, var desc: String) {
        @IntentType
        var type = ArchConstants.INTENT_TYPE_ACTIVITY
        var perm: String? = null

        constructor(@IntentType type: Int, intent: Intent, title: String, desc: String) : this(
            intent,
            title,
            desc
        ) {
            this.type = type
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
        if (needLoadIntentsAsync()) {
            object : AsyncTask<Void, Void, List<IntentEntry>>() {
                override fun doInBackground(vararg params: Void): List<IntentEntry> {
                    return intents
                }

                override fun onPostExecute(result: List<IntentEntry>) {
                    adapter.setData(intents)
                }
            }.execute()
        } else {
            adapter.setData(intents)
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = adapter.getItem(position)
        onItemClicked(item)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        val item = adapter.getItem(position)
        ToastHelper.show(this, item.desc, Toast.LENGTH_LONG)
        return true
    }

    /**
     * Decide if we need to invoke [.getIntent] async.
     * @return true for async and false for sync. false by default
     */
    protected open fun needLoadIntentsAsync(): Boolean {
        return false
    }

    protected open fun onItemClicked(item: IntentEntry) {
        if (IntentUtils.canStartActivity(this, item.intent)) {
            startActivity(item.intent)
        } else {
            ToastHelper.show(this, item.desc, Toast.LENGTH_LONG)
        }
    }

    protected open class SystemEntriesAdapter(cxt: Context) :
        ListAdapterBase<IntentEntry, SystemEntriesAdapter.ViewHolder>(cxt) {

        override val itemLayoutResId: Int
            get() = R.layout.commonui_grid_entries_item

        override fun createViewHolder(itemView: View, position: Int): ViewHolder {
            return ViewHolder(itemView, position)
        }

        override fun bindView(item: IntentEntry, holder: ViewHolder) {
            holder.titleView.text = item.title
        }

        class ViewHolder(itemView: View, position: Int) :
            ViewHolderBase(itemView, position) {
            var titleView: TextView = itemView.findViewById(R.id.title)
        }
    }
}
