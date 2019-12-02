package com.hykj.base.adapter.pager;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
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
    private final @LayoutRes
    int mLayoutId;
    private OnItemClickListener mListener;
    private OnItemLongClickListener onItemLongClickListener;
    private LinkedList<View> mViews = new LinkedList<>();//回收利用View
    private boolean isRecycle = true;//是否回收利用View
    private int mChildCount;

    public BasePagerAdapter(Context context, List<T> datas, @LayoutRes int layoutId) {
        this.mContext = context;
        this.mDatas = datas == null ? new ArrayList<T>() : datas;
        this.mLayoutId = layoutId;
    }

    public BasePagerAdapter(Context context, List<T> datas, @LayoutRes int layoutId, boolean isRecycle) {
        this.mContext = context;
        this.mDatas = datas == null ? new ArrayList<T>() : datas;
        this.mLayoutId = layoutId;
        this.isRecycle = isRecycle;
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
        ViewHolder holder = ViewHolder.get(mContext, mViews, mLayoutId, position, this, mListener, onItemLongClickListener);
        this.convert(holder, this.mDatas.get(position), position);
        container.addView(holder.getContentView());
        return holder.getContentView();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        destroyItemEx(container, position, (View) object);
        container.removeView((View) object);
        if (isRecycle)
            mViews.addLast((View) object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    protected abstract void convert(ViewHolder holder, T t, int position);

    protected void destroyItemEx(ViewGroup container, int position, View view) {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(BasePagerAdapter adapter, View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener {
        void OnItemLongClick(BasePagerAdapter adapter, View view, int position);
    }

    /**
     * 导入数据
     *
     * @param data    新数据
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

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mChildCount > 0) {//跟notifyDataSetChanged()方法组合，用于ViewPager不切换页面时调用返回POSITION_NONE刷新数据
            mChildCount = 0;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
