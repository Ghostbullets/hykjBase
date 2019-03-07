package com.hykj.base.adapter.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * created by cjf
 * on:2019/3/7 10:42
 * PagerAdapter视图持有者
 */
public class ViewHolder {
    private int mPosition;
    private View mConvertView;
    private SparseArray<View> mViews;

    public ViewHolder(Context context, ViewGroup container, int layoutId, int position, final BasePagerAdapter adapter, final BasePagerAdapter.OnItemClickListener listener) {
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
        if (this.mConvertView.getLayoutParams() == null) {
            this.mConvertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        container.addView(this.mConvertView);
    }

    public static ViewHolder get(Context context, ViewGroup container, View convertView, int layoutId, int position, BasePagerAdapter adapter, BasePagerAdapter.OnItemClickListener listener) {
        return convertView == null ? new ViewHolder(context, container, layoutId, position, adapter, listener) : (ViewHolder) convertView.getTag();
    }

    public ViewHolder setText(int viewId, String text) {
        TextView view = this.getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bm);
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
