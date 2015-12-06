package me.ycdev.android.lib.commonui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.ycdev.android.arch.ArchConstants;
import me.ycdev.android.arch.activity.AppCompatBaseActivity;
import me.ycdev.android.arch.wrapper.ToastHelper;
import me.ycdev.android.lib.common.utils.IntentUtils;
import me.ycdev.android.lib.commonui.R;
import me.ycdev.android.lib.commonui.base.ListAdapterBase;

import static me.ycdev.android.arch.ArchConstants.IntentType;

public abstract class GridEntriesActivity extends AppCompatBaseActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static class IntentEntry {
        public @NonNull Intent intent;
        public @NonNull String title;
        public @NonNull String desc;
        public @IntentType int type = ArchConstants.INTENT_TYPE_ACTIVITY;
        public @Nullable String perm;

        public IntentEntry(@NonNull Intent intent, @NonNull String title, @NonNull String desc) {
            this.intent = intent;
            this.title = title;
            this.desc = desc;
        }

        public IntentEntry(@IntentType int type, Intent intent, String title, String desc) {
            this(intent, title, desc);
            this.type = type;
        }
    }

    protected SystemEntriesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonui_grid_entries);

        mAdapter = new SystemEntriesAdapter(this);

        GridView gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        loadItems();
    }

    private void loadItems() {
        if (needLoadIntentsAsync()) {
            new AsyncTask<Void, Void, List<IntentEntry>>() {
                @Override
                protected List<IntentEntry> doInBackground(Void... params) {
                    return getIntents();
                }

                @Override
                protected void onPostExecute(List<IntentEntry> result) {
                    mAdapter.setData(getIntents());
                }
            }.execute();
        } else {
            mAdapter.setData(getIntents());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IntentEntry item = mAdapter.getItem(position);
        onItemClicked(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        IntentEntry item = mAdapter.getItem(position);
        ToastHelper.show(this, item.desc, Toast.LENGTH_LONG);
        return true;
    }

    /**
     * Decide if we need to invoke {@link #getIntent()} async.
     * @return true for async and false for sync. false by default
     */
    protected boolean needLoadIntentsAsync() {
        return false;
    }

    protected abstract List<IntentEntry> getIntents();

    protected void onItemClicked(IntentEntry item) {
        if (IntentUtils.canStartActivity(this, item.intent)) {
            startActivity(item.intent);
        } else {
            ToastHelper.show(this, item.desc, Toast.LENGTH_LONG);
        }
    }

    protected static class SystemEntriesAdapter extends ListAdapterBase<IntentEntry>  {
        public SystemEntriesAdapter(Context cxt) {
            super(cxt);
        }

        @Override
        protected int getItemLayoutResId() {
            return R.layout.commonui_grid_entries_item;
        }

        @NonNull
        @Override
        protected ViewHolderBase createViewHolder(@NonNull View itemView, int position) {
            return new ViewHolder(itemView, position);
        }

        @Override
        protected void bindView(@NonNull IntentEntry item, @NonNull ViewHolderBase holder) {
            ViewHolder vh = (ViewHolder) holder;
            vh.titleView.setText(item.title);
        }

        protected static class ViewHolder extends ListAdapterBase.ViewHolderBase {
            public TextView titleView;

            public ViewHolder(@NonNull View itemView, int position) {
                super(itemView, position);
            }

            @Override
            protected void findViews() {
                titleView = (TextView) itemView.findViewById(R.id.title);
            }
        }

    }
}
