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
     * @param tabLayout     控件
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
                        Field textViewField = null;
                        try {
                            textViewField = tabView.getClass().getDeclaredField("mTextView");//27.1.1及以下tabView里面的TextView参数名是mTextView
                        } catch (NoSuchFieldException e) {
                            try {
                                textViewField = tabView.getClass().getDeclaredField("textView");//28.0.0及以上tabView里面的TextView参数名是textView
                            } catch (NoSuchFieldException e1) {

                            }
                        }
                        if (textViewField == null)
                            return;
                        textViewField.setAccessible(true);
                        TextView textView = (TextView) textViewField.get(tabView);

                        //测量mTextView的宽度
                        int width = textView.getWidth();
                        if (width <= 0) {
                            textView.measure(0, 0);
                            width = textView.getMeasuredWidth();
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
