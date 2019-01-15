package com.hykj.base.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LayoutAdapter<T> {
    private List<T> mDatas;
    private @LayoutRes int resId;
    private OnDataChangedListener mOnDataChangedListener;

    public LayoutAdapter(List<T> mDatas, int resId) {
        this.mDatas = mDatas;
        this.resId = resId;
    }

    public LayoutAdapter(T[] datas,int resId) {
        mDatas = new ArrayList<>(Arrays.asList(datas));
        this.resId = resId;
    }

    public interface OnDataChangedListener {
        void onChanged();
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }


    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void notifyDataChanged() {
        mOnDataChangedListener.onChanged();
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    public int getResId() {
        return resId;
    }

    public abstract void convert(View child, int position, T t);

    /**
     * 更新数据
     *
     * @param list    传入的数据
     * @param isClear 是否清空之前的数据
     */
    public void reloadListView(List<T> list, boolean isClear) {
        if (isClear) {
            mDatas.clear();
        }
        mDatas.addAll(list);
        notifyDataChanged();
    }
}