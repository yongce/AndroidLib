package me.ycdev.android.lib.common.ui.base;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListAdapterBase<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected List<T> mList;

    public ListAdapterBase(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public void setData(List<T> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void sort(Comparator<T> comparator) {
        Collections.sort(mList, comparator);
        notifyDataSetChanged();
    }

    /**
     * @return null will be returned if no data set.
     */
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
        ViewHolderBase holder = null;
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
    protected abstract ViewHolderBase createViewHolder(View itemView, int position);
    protected abstract void bindView(T item, ViewHolderBase holder);

    protected static abstract class ViewHolderBase {
        public View itemView;
        public int position;

        public ViewHolderBase(View itemView, int position) {
            this.itemView = itemView;
            this.position = position;
            findViews();
        }

        protected abstract void findViews();
    }
}
