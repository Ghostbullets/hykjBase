package com.hykj.base.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 万能适配器
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int mLayoutId) {
        this.mContext = context;
        this.mInflater=LayoutInflater.from(this.mContext);
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(this.mContext, convertView, parent, this.mLayoutId,position);
        this.convert(viewHolder,this.getItem(position),position);
        return viewHolder.getConvertView();
    }

    public abstract void convert(ViewHolder viewHolder,T t,int position);

    /**
     * 导入数据
     * @param data 新数据
     * @param isClear 是否清空原有数据
     */
    public void reloadListView(List<T> data, boolean isClear) {
        if (isClear) {
            this.mDatas.clear();
        }
        if (data != null) {
            this.mDatas.addAll(data);
        }
        notifyDataSetChanged();
    }
}
