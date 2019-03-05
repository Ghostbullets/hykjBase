package com.hykj.base.adapter.pager;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cjf
 * on:2019/3/5 17:21
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {
    protected static final String TAG = BasePagerAdapter.class.getName();
    protected List<T> mDatas;
    protected View mCurrentView;

    public BasePagerAdapter(List<T> datas) {
        this.mDatas = datas == null ? new ArrayList<T>() : datas;
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
        return instantiateItem(container, position, mDatas.get(position));
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        destroyItemEx(container, position, object);
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

    protected abstract Object instantiateItem(ViewGroup container, int position, T t);

    protected void destroyItemEx(ViewGroup container, int position, Object object) {
    }
}
