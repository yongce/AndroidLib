package me.ycdev.android.lib.commonui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ycdev.android.lib.common.utils.IntentUtils
import me.ycdev.android.lib.common.utils.IntentUtils.INTENT_TYPE_ACTIVITY
import me.ycdev.android.lib.common.utils.IntentUtils.INTENT_TYPE_BROADCAST
import me.ycdev.android.lib.common.wrapper.BroadcastHelper
import me.ycdev.android.lib.commonui.R
import me.ycdev.android.lib.commonui.databinding.YcdevGridEntriesItemBinding
import me.ycdev.android.lib.commonui.recyclerview.MarginItemDecoration

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class GridEntriesActivity : AppCompatActivity() {

    protected lateinit var entriesAdapter: SystemEntriesAdapter
    protected lateinit var gridView: RecyclerView
    protected lateinit var loadingView: ProgressBar

    protected open val contentViewLayout: Int
        @LayoutRes get() = R.layout.ycdev_grid_entries

    protected abstract fun loadIntents(): List<Entry>

    open class Entry(
        open val title: CharSequence,
        open val desc: CharSequence,
        open val clickAction: ((Context) -> Unit)? = null,
        open val longClickAction: ((Context) -> Unit)? = null
    )

    open class IntentEntry(
        @IntentUtils.IntentType val type: Int = INTENT_TYPE_ACTIVITY,
        val intent: Intent,
        title: String,
        desc: String,
        val perm: String? = null
    ) : Entry(title, desc) {
        constructor(intent: Intent, title: String, desc: String) :
            this(INTENT_TYPE_ACTIVITY, intent, title, desc)

        override val clickAction: ((Context) -> Unit)? = ::onItemClicked
        override val longClickAction: ((Context) -> Unit)? = ::onItemLongClicked

        protected open fun onItemClicked(context: Context) {
            if (type == INTENT_TYPE_ACTIVITY) {
                IntentUtils.startActivity(context, intent)
            } else if (type == INTENT_TYPE_BROADCAST) {
                BroadcastHelper.sendToExternal(context, intent, perm)
            }
        }

        protected open fun onItemLongClicked(context: Context) {
            Toast.makeText(context, desc, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewLayout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        entriesAdapter = SystemEntriesAdapter(this)

        gridView = findViewById(R.id.grid)
        loadingView = findViewById(R.id.progress)

        gridView.apply {
            adapter = entriesAdapter
            layoutManager = GridLayoutManager(this@GridEntriesActivity, 3)
            addItemDecoration(MarginItemDecoration.create(getGridEntriesMargin()))
        }

        loadItems()
    }

    open fun getGridEntriesMargin(): Int {
        val a = obtainStyledAttributes(intArrayOf(R.attr.ycdevGridEntriesItemMargin))
        val margin: Int = a.getDimensionPixelSize(0, 0)
        a.recycle()
        return margin
    }

    @SuppressLint("StaticFieldLeak", "NotifyDataSetChanged")
    protected open fun loadItems() {
        if (needLoadIntentsAsync) {
            loadingView.visibility = View.VISIBLE
            lifecycleScope.launch {
                val intents: List<Entry>
                withContext(Dispatchers.Default) {
                    intents = loadIntents()
                }

                loadingView.visibility = View.GONE
                entriesAdapter.data = intents
                entriesAdapter.notifyDataSetChanged()
            }
        } else {
            loadingView.visibility = View.GONE
            entriesAdapter.data = loadIntents()
        }
    }

    /**
     * Decide if we need to invoke [.getIntent] async.
     * @return true for async and false for sync. false by default
     */
    protected open val needLoadIntentsAsync: Boolean = false

    protected open class SystemEntriesAdapter(val context: Context) :
        RecyclerView.Adapter<SystemEntriesAdapter.ViewHolder>() {

        var data: List<Entry>? = null

        private fun getItem(position: Int): Entry {
            return data!![position]
        }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context)
                .inflate(R.layout.ycdev_grid_entries_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            holder.binding.title.text = item.title

            holder.binding.root.setOnClickListener { item.clickAction?.invoke(context) }
            holder.binding.root.setOnLongClickListener {
                item.longClickAction?.invoke(context)
                return@setOnLongClickListener true
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding: YcdevGridEntriesItemBinding = YcdevGridEntriesItemBinding.bind(itemView)
        }
    }
}
