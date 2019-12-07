package com.hykj.base.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * created by cjf
 * on:2019/3/25 11:00
 * Viewpager设置高度为wrap_content时使用的自定义控件
 */
public class WrapHeightViewPager extends ViewPager {
    private boolean isMeasureHeight = true;

    public WrapHeightViewPager(@NonNull Context context) {
        super(context);
    }

    public WrapHeightViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isMeasureHeight) {
            int maxHeight = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                childAt.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = childAt.getMeasuredHeight();
                if (h > maxHeight)
                    maxHeight = h;
            }
            maxHeight += getPaddingTop() + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMeasureHeight(boolean isMeasureHeight) {
        this.isMeasureHeight = isMeasureHeight;
    }
}
