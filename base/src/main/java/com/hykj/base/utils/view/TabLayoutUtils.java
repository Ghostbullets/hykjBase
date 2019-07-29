package com.hykj.base.utils.view;


import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * TabLayout工具类
 */
public class TabLayoutUtils {

    public static void reflex(final TabLayout tabLayout, int bottomMargin) {
        reflex(tabLayout, bottomMargin, true);
    }

    /**
     * 重新设置TabLayout的margin
     *
     * @param tabLayout 控件
     * @param bottomMargin  顶部文字跟底部下划线距离
     * @param isEqualLength 下划线是否跟文字等长
     */
    public static void reflex(final TabLayout tabLayout, final int bottomMargin, final boolean isEqualLength) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);//拿到mTabStrip属性
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        tabView.setPadding(0, 0, 0, 0);
                        int tabViewWidth = tabView.getWidth();
                        if (tabViewWidth <= 0) {
                            tabView.measure(0, 0);
                            tabViewWidth = tabView.getMeasuredWidth();
                        }

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        //测量mTextView的宽度
                        int width = mTextView.getWidth();
                        if (width <= 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        int margin = isEqualLength ? (tabViewWidth - width) / 2 : (tabViewWidth - width) / 4;

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.setMargins(margin, 0, margin, bottomMargin);
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
