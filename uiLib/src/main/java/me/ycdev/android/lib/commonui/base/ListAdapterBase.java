package me.ycdev.android.lib.commonui.base;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ListAdapterBase<ItemType, VH extends ViewHolderBase> extends BaseAdapter {
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<ItemType> mList;

    public ListAdapterBase(@NonNull Context cxt) {
        mContext = cxt;
        if (cxt instanceof Activity) {
            mInflater = ((Activity) cxt).getLayoutInflater();
        } else {
            mInflater = LayoutInflater.from(cxt);
        }
    }

    public void setData(@Nullable List<ItemType> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void sort(@NonNull Comparator<ItemType> comparator) {
        Collections.sort(mList, comparator);
        notifyDataSetChanged();
    }

    /**
     * @return null will be returned if no data set.
     */
    @Nullable
    public List<ItemType> getData() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size(): 0;
    }

    @Override
    public ItemType getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(getItemLayoutResId(), parent, false);
            holder = createViewHolder(convertView, position);
            convertView.setTag(holder);
        } else {
            @SuppressWarnings("unchecked")
            VH tmp = (VH) convertView.getTag();
            holder = tmp;
        }
        bindView(getItem(position), holder);
        return convertView;
    }

    protected abstract @LayoutRes int getItemLayoutResId();
    protected abstract @NonNull VH createViewHolder(@NonNull View itemView, int position);
    protected abstract void bindView(@NonNull ItemType item, @NonNull VH holder);
}
