package com.hykj.base.adapter.listview;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
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


    public <T extends View> T getView(@IdRes int viewId) {
        View view = this.mViews.get(viewId);
        if (view == null) {
            view = this.mConvertView.findViewById(viewId);
            this.mViews.put(viewId, view);
        }
        return (T) view;
    }

    public ViewHolder setText(@IdRes int viewId, CharSequence text) {
        TextView view = this.getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setImageResource(@IdRes int viewId, @DrawableRes int drawableId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public ViewHolder setImageBitmap(@IdRes int viewId, Bitmap bm) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public ViewHolder setVisibility(@IdRes int viewId, int visibility) {
        if (visibility == View.VISIBLE || visibility == View.INVISIBLE || visibility == View.GONE)
            this.getView(viewId).setVisibility(visibility);
        return this;
    }

    public ViewHolder setLayoutParams(@IdRes int viewId, int width, int height) {
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

    public ViewHolder setLayoutParams(@IdRes int viewId, ViewGroup.LayoutParams params) {
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

    public ViewHolder setOnClickListener(@IdRes int viewId, Object o, View.OnClickListener listener) {
        View view = this.getView(viewId);
        view.setTag(o);
        view.setOnClickListener(listener);
        return this;
    }

    public int getPosition() {
        return this.position;
    }
}
