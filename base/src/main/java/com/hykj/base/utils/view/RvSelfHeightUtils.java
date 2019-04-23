package com.hykj.base.utils.view;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.ReflectionUtils;

/**
 * created by cjf
 * on:2019/4/23 11:02
 * 自适应高度RecyclerView，设置RecyclerView最大高度不能高于1个限定值，最小可为0，即高度范围0-限定值
 */
public class RvSelfHeightUtils {
    private RecyclerView rv;
    private int maximumHeight;

    public RvSelfHeightUtils(RecyclerView rv, View... views) {
        this.rv = rv;
        setMaximumHeight(0, views);
    }

    public RvSelfHeightUtils(RecyclerView rv, final int offsetSizeHeight, View... views) {
        this.rv = rv;
        setMaximumHeight(offsetSizeHeight, views);
    }

    /**
     * 用于控制RecycleView的高度
     * 请在设置了适配器并且获取到数据以后调用该方法(注:xml中RecycleView需要设置为固定值，如果设置为wrap则方法可能无效)
     * int maximumHeight=(屏幕高度-它的父控件中除了它以外的其他所有子控件的高度)
     * int measureHeight=RecycleView的所有item的总高度
     * 设置RecycleView的最大高度
     * 当measureHeight大于maximumHeight时，设置RecycleView的LayoutParams.height为maximumHeight
     * 其他情况下设置RecycleView的LayoutParams.height为measureHeight
     *
     * @param offsetSizeHeight 偏差值，maximumHeight会减去该值
     * @param views            想要减去的其他View的高度
     */
    private void setMaximumHeight(final int offsetSizeHeight, final View... views) {
        rv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int itemCount = 0;
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter != null)
                    itemCount = adapter.getItemCount();

                int measuredHeight = 0;//RecycleView所有item的总高度
                maximumHeight = new DisplayUtils().screenHeight();//最大高度最先设置为屏幕高度
                ViewGroup parent = (ViewGroup) rv.getParent();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (child == rv) {
                        if (itemCount > 0) {
                            for (int j = 0; j < rv.getChildCount(); j++) {
                                View childAt = rv.getChildAt(j);
                                if (childAt != null) {
                                    measuredHeight += getViewHeightWithMargin(childAt);
                                }
                            }
                        }
                    } else {
                        maximumHeight -= getViewHeightWithMargin(child);
                    }
                }
                for (View view : views) {
                    maximumHeight -= getViewHeightWithMargin(view);
                }
                if (rv.getItemDecorationCount() > 0) {
                    DividerItemDecoration itemDecoration = (DividerItemDecoration) rv.getItemDecorationAt(0);
                    Drawable drawable = (Drawable) ReflectionUtils.getFieldValue(itemDecoration, "mDivider");
                    if (drawable != null) {
                        measuredHeight += drawable.getIntrinsicHeight() * itemCount;
                    }
                }
                maximumHeight -= offsetSizeHeight;//减去偏差值
                ViewGroup.LayoutParams params = rv.getLayoutParams();
                if (measuredHeight > maximumHeight) {
                    params.height = maximumHeight;
                } else {
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                }
                rv.setLayoutParams(params);
            }
        });
    }

    public void updateUI() {
        rv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int itemCount = 0;
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter != null)
                    itemCount = adapter.getItemCount();

                int measuredHeight = 0;//RecycleView所有item的总高度
                if (itemCount > 0) {
                    for (int j = 0; j < rv.getChildCount(); j++) {
                        View childAt = rv.getChildAt(j);
                        if (childAt != null) {
                            measuredHeight += getViewHeightWithMargin(childAt);
                        }
                    }
                }
                if (rv.getItemDecorationCount() > 0) {
                    DividerItemDecoration itemDecoration = (DividerItemDecoration) rv.getItemDecorationAt(0);
                    Drawable drawable = (Drawable) ReflectionUtils.getFieldValue(itemDecoration, "mDivider");
                    if (drawable != null) {
                        measuredHeight += drawable.getIntrinsicHeight() * itemCount;
                    }
                }
                ViewGroup.LayoutParams params = rv.getLayoutParams();
                if (measuredHeight > maximumHeight) {
                    params.height = maximumHeight;
                } else {
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                }
                rv.setLayoutParams(params);
            }
        });
    }

    public static int getViewHeightWithMargin(View view) {
        int height = view.getMeasuredHeight();
        if (height <= 0) {
            view.measure(-1, -1);
            height = view.getMeasuredHeight();
        }
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            height = height + params.topMargin + params.bottomMargin;
        }
        return height;
    }
}
