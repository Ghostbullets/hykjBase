package com.hykj.base.utils.view;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.ReflectionUtils;

public class RecycleViewUtils {

    public static void setMaximumHeight(final RecyclerView rv, final View... views) {
        setMaximumHeight(rv, 0, views);
    }

    /**
     * 用于控制RecycleView的高度
     * 请在设置了适配器并且获取到数据以后调用该方法
     * int maximumHeight=(屏幕高度-它的父控件中除了它以外的其他所有子控件的高度)
     * int measureHeight=RecycleView的所有item的总高度
     * 设置RecycleView的最大高度
     * 当measureHeight大于maximumHeight时，设置RecycleView的LayoutParams.height为maximumHeight
     * 其他情况下设置RecycleView的LayoutParams.height为measureHeight
     *
     * @param rv               控件
     * @param offsetSizeHeight 偏差值，maximumHeight会减去该值
     * @param views            想要减去的其他View的高度
     */
    public static void setMaximumHeight(final RecyclerView rv, final int offsetSizeHeight, final View... views) {
        rv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int itemCount = 0;
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter != null)
                    itemCount = adapter.getItemCount();

                int measuredHeight = 0;//RecycleView所有item的总高度
                int maximumHeight = new DisplayUtils().screenHeight();//最大高度最先设置为屏幕高度
                ViewGroup parent = (ViewGroup) rv.getParent();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (child == rv) {
                        if (itemCount > 0) {
                            View childAt = rv.getChildAt(0);
                            measuredHeight = childAt.getMeasuredHeight();
                            if (measuredHeight <= 0) {
                                childAt.measure(-1, -1);
                                measuredHeight = childAt.getMeasuredHeight();
                            }
                        }
                    } else {
                        maximumHeight -= child.getMeasuredHeight();
                    }
                }
                for (View view : views) {
                    int height = view.getMeasuredHeight();
                    if (height <= 0) {
                        view.measure(-1, -1);
                        height = view.getMeasuredHeight();
                    }
                    maximumHeight -= height;
                }
                if (rv.getItemDecorationCount() > 0) {
                    DividerItemDecoration itemDecoration = (DividerItemDecoration) rv.getItemDecorationAt(0);
                    Drawable drawable = (Drawable) ReflectionUtils.getFieldValue(itemDecoration, "mDivider");
                    if (drawable != null) {
                        measuredHeight += drawable.getIntrinsicHeight();
                    }
                }
                measuredHeight = measuredHeight * itemCount;
                maximumHeight -= offsetSizeHeight;//减去偏差值
                ViewGroup.LayoutParams params = rv.getLayoutParams();
                if (measuredHeight > maximumHeight) {
                    params.height = maximumHeight;
                } else {
                    params.height = measuredHeight;
                }
                rv.setLayoutParams(params);
            }
        });
    }
}
