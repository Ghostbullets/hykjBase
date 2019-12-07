package com.hykj.base.adapter.recycleview;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hykj.base.adapter.LayoutItem;

import java.util.List;

/**
 * 多布局适配器
 * @param <T>
 */
public abstract class MultiRecycleViewAdapter<T> extends BaseAdapter<T, BaseViewHolder> {
    protected SparseIntArray mLayoutArray = new SparseIntArray();

    public MultiRecycleViewAdapter(Context context, List<T> datas, LayoutItem... layoutItems) {
        super(context, datas, 0);
        for (int i = 0; i < layoutItems.length; i++) {
            LayoutItem item = layoutItems[i];
            mLayoutArray.put(item.getType(), item.getLayoutId());
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutArray.get(viewType), parent, false);
        return new BaseViewHolder(itemView, mListener);
    }

    @Override
    public int getItemViewType(int position) {
        return this.getItemViewType(this.getItem(position), position);
    }

    public abstract int getItemViewType(T t, int position);
}
