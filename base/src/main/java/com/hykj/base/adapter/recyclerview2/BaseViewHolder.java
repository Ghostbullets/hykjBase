package com.hykj.base.adapter.recyclerview2;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 万能ViewHolder适配器RecycleView
 * Created by Administrator on 2018/1/11.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private BaseAdapter.OnItemClickListener mListener;
    private BaseAdapter.OnItemLongClickListener onItemLongClickListener;
    private BaseAdapter mAdapter;
    private SparseArray<View> mViews;

    public BaseViewHolder(BaseAdapter adapter, View itemView, BaseAdapter.OnItemClickListener itemClickListener, BaseAdapter.OnItemLongClickListener onItemLongClickListener) {
        super(itemView);
        this.mAdapter = adapter;
        this.mListener = itemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
        this.mViews = new SparseArray<>();
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }


    public BaseViewHolder setText(@IdRes int viewId, CharSequence text) {
        TextView view = this.getView(viewId);
        view.setText(text);
        return this;
    }

    public BaseViewHolder setImageResource(@IdRes int viewId, @DrawableRes int drawableId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public BaseViewHolder setImageBitmap(@IdRes int viewId, Bitmap bm) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public BaseViewHolder setVisibility(@IdRes int viewId, int visibility) {
        if (visibility == View.VISIBLE || visibility == View.INVISIBLE || visibility == View.GONE)
            this.getView(viewId).setVisibility(visibility);
        return this;
    }

    public BaseViewHolder setLayoutParams(@IdRes int viewId, int width, int height) {
        if ((width == -1 || width == -2 || width > 0) && (height == -1 || height == -2 || height > 0)) {
            View view = this.getView(viewId);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.MarginLayoutParams(width, height);
            } else {
                params.width = width;
                params.height = height;
            }
            view.setLayoutParams(params);
        }
        return this;
    }

    public BaseViewHolder setLayoutParams(@IdRes int viewId, ViewGroup.LayoutParams params) {
        if (params != null) {
            View view = this.getView(viewId);
            if (view.getParent() instanceof ViewGroup) {
                ViewGroup.LayoutParams layoutParams = ((ViewGroup) view.getParent()).getLayoutParams();
                if (layoutParams != null && layoutParams.getClass().getName().equals(params.getClass().getName())) {
                    view.setLayoutParams(params);
                }
            }
        }
        return this;
    }

    public BaseViewHolder setOnClickListener(@IdRes int viewId, Object o, View.OnClickListener listener) {
        View view = this.getView(viewId);
        view.setTag(o);
        view.setOnClickListener(listener);
        return this;
    }

    public <T extends View> T getView(@IdRes int id) {
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

    @Override
    public boolean onLongClick(View v) {
        if (onItemLongClickListener != null)
            onItemLongClickListener.OnItemLongClick(mAdapter, v, getLayoutPosition());
        return onItemLongClickListener != null;
    }
}
