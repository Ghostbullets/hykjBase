package com.hykj.base.adapter;

import androidx.annotation.LayoutRes;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LayoutAdapter<T> {
    private List<T> mDatas;
    private @LayoutRes
    int resId;
    private OnDataChangedListener mOnDataChangedListener;

    public LayoutAdapter(List<T> mDatas, int resId) {
        this.mDatas = mDatas == null ? new ArrayList<T>() : mDatas;
        this.resId = resId;
    }

    public LayoutAdapter(int resId, T[] datas) {
        mDatas = datas == null || datas.length == 0 ? new ArrayList<T>() : new ArrayList<>(Arrays.asList(datas));
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
        if (mOnDataChangedListener != null)
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