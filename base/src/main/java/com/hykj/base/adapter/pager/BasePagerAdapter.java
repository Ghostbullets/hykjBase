package com.hykj.base.adapter.pager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cjf
 * on:2019/3/5 17:21
 * 万能PagerAdapter适配器
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {
    protected static final String TAG = BasePagerAdapter.class.getName();
    protected View mCurrentView;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mLayoutId;
    private OnItemClickListener mListener;

    public BasePagerAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        this.mDatas = datas == null ? new ArrayList<T>() : datas;
        this.mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return this.mDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewHolder holder = ViewHolder.get(mContext, container, mCurrentView, mLayoutId, position, this, mListener);
        this.convert(holder, this.mDatas.get(position), position);
        return holder.getContentView();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        destroyItemEx(container, position, (View) object);
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    protected abstract void convert(ViewHolder viewHolder, T t, int position);

    protected void destroyItemEx(ViewGroup container, int position, View view) {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(BasePagerAdapter adapter, View view, int position);
    }
}
