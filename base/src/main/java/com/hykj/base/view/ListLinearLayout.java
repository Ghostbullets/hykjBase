package com.hykj.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.hykj.base.adapter.LayoutAdapter;

/**
 * 适配器ListLinearLayout
 */
public class ListLinearLayout extends LinearLayout implements LayoutAdapter.OnDataChangedListener {
    private LayoutAdapter mAdapter;

    public ListLinearLayout(Context context) {
        super(context);
    }

    public ListLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setAdapter(LayoutAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setOnDataChangedListener(this);
        onChanged();
    }

    @Override
    public void onChanged() {
        removeAllViews();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = View.inflate(getContext(), mAdapter.getResId(), null);
            mAdapter.convert(view, i, mAdapter.getItem(i));
            if (view.getLayoutParams() == null) {
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
                view.setLayoutParams(params);
            }
            addView(view);
        }
    }
}
