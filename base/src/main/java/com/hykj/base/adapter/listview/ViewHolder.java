package com.hykj.base.adapter.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 万能适配器 ViewHolder
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private int position;

    private ViewHolder(Context context, ViewGroup parent, int mLayoutId, int position) {
        this.position = position;
        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(mLayoutId, parent, false);
        this.mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int mLayoutId, int position) {
        return convertView == null ? new ViewHolder(context, parent, mLayoutId, position) : (ViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return this.mConvertView;
    }


    public <T extends View> T getView(int viewId) {
        View view = this.mViews.get(viewId);
        if (view == null) {
            view = this.mConvertView.findViewById(viewId);
            this.mViews.put(viewId, view);
        }
        return (T) view;
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

    public int getPosition() {
        return this.position;
    }
}
