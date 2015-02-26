package me.ycdev.android.lib.common.ui.base;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListAdapterBase<T> extends BaseAdapter {
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mList;

    public ListAdapterBase(@NonNull Context cxt) {
        mContext = cxt;
        if (cxt instanceof Activity) {
            mInflater = ((Activity) cxt).getLayoutInflater();
        } else {
            mInflater = LayoutInflater.from(cxt);
        }
    }

    public void setData(@Nullable List<T> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void sort(@NonNull Comparator<T> comparator) {
        Collections.sort(mList, comparator);
        notifyDataSetChanged();
    }

    /**
     * @return null will be returned if no data set.
     */
    @Nullable
    public List<T> getData() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size(): 0;
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderBase holder;
        if (convertView == null) {
            convertView = mInflater.inflate(getItemResId(), parent, false);
            holder = createViewHolder(convertView, position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderBase) convertView.getTag();
        }
        bindView(getItem(position), holder);
        return convertView;
    }

    protected abstract int getItemResId();
    protected abstract ViewHolderBase createViewHolder(@NonNull View itemView, int position);
    protected abstract void bindView(T item, @NonNull ViewHolderBase holder);

    protected static abstract class ViewHolderBase {
        @NonNull
        public View itemView;
        public int position;

        public ViewHolderBase(@NonNull View itemView, int position) {
            this.itemView = itemView;
            this.position = position;
            findViews();
        }

        protected abstract void findViews();
    }
}
