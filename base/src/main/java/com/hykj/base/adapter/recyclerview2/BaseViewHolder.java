package com.hykj.base.adapter.recyclerview2;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 万能ViewHolder适配器RecycleView
 * Created by Administrator on 2018/1/11.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private BaseAdapter.OnItemClickListener mListener;
    private BaseAdapter mAdapter;
    private SparseArray<View> mViews;

    public BaseViewHolder(BaseAdapter adapter, View itemView, BaseAdapter.OnItemClickListener itemClickListener) {
        super(itemView);
        this.mAdapter = adapter;
        this.mListener = itemClickListener;
        this.mViews = new SparseArray<>();
        itemView.setOnClickListener(this);
    }


    public BaseViewHolder setText(int viewId, CharSequence text) {
        TextView view = this.getView(viewId);
        view.setText(text);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public BaseViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public BaseViewHolder setOnClickListener(int viewId, Object o, View.OnClickListener listener) {
        View view = this.getView(viewId);
        view.setTag(o);
        view.setOnClickListener(listener);
        return this;
    }

    public <T extends View> T getView(int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.OnItemClick(mAdapter, view, getLayoutPosition());
        }
    }
}
