package com.hykj.base.adapter.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * created by cjf
 * on:2019/3/7 10:42
 * PagerAdapter视图持有者
 */
public class ViewHolder {
    private int mPosition;
    private View mConvertView;
    private SparseArray<View> mViews;

    public ViewHolder(Context context, @LayoutRes int layoutId, int position, final BasePagerAdapter adapter, final BasePagerAdapter.OnItemClickListener listener, final BasePagerAdapter.OnItemLongClickListener onItemLongClickListener) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, null);
        this.mConvertView.setTag(this);
        this.mConvertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.OnItemClick(adapter, mConvertView, mPosition);
            }
        });
        this.mConvertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null)
                    onItemLongClickListener.OnItemLongClick(adapter, mConvertView, mPosition);
                return onItemLongClickListener != null;
            }
        });
    }

    public static ViewHolder get(Context context, LinkedList<View> mViews, int layoutId, int position, BasePagerAdapter adapter, BasePagerAdapter.OnItemClickListener listener, BasePagerAdapter.OnItemLongClickListener onItemLongClickListener) {
        return mViews.size() > 0 ? (ViewHolder) mViews.removeFirst().getTag() : new ViewHolder(context, layoutId, position, adapter, listener, onItemLongClickListener);
    }

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView view = this.getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setImageResource(int viewId, @DrawableRes int drawableId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public ViewHolder setOnClickListener(int viewId, Object o, View.OnClickListener listener) {
        View view = this.getView(viewId);
        view.setTag(o);
        view.setOnClickListener(listener);
        return this;
    }

    public <T extends View> T getView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = mConvertView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (T) view;
    }

    public int getPosition() {
        return mPosition;
    }

    public View getContentView() {
        return mConvertView;
    }
}
