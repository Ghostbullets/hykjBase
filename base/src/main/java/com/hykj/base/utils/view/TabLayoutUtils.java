package com.hykj.base.utils.view;


import android.graphics.Typeface;
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
        reflex(tabLayout, true);
    }

    /**
     * 重新设置TabLayout的margin，仅用于设置下划线的长度在  textView或者customView宽度  到 tabView宽度  之间
     *
     * @param tabLayout     控件
     * @param isEqualLength 下划线是否跟文字等长
     */
    public static void reflex(final TabLayout tabLayout, final boolean isEqualLength) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);//拿到27.1.1mTabStrip、28.0.0slidingTabIndicator属性
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        tabView.setPadding(0, 0, 0, 0);
                        int tabViewWidth = tabView.getWidth();
                        if (tabViewWidth <= 0) {
                            tabView.measure(0, 0);
                            tabViewWidth = tabView.getMeasuredWidth();
                        }
                        Field customViewField = null;
                        try {
                            customViewField = tabView.getClass().getDeclaredField("mCustomView");
                        } catch (Exception e) {
                            try {
                                customViewField = tabView.getClass().getDeclaredField("customView");
                            } catch (Exception e1) {

                            }
                        }
                        View view = null;
                        if (customViewField != null) {
                            customViewField.setAccessible(true);
                            view = (View) customViewField.get(tabView);
                        }
                        if (view == null) {
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
                            view = (TextView) textViewField.get(tabView);
                        }

                        //测量mTextView、mCustomView的宽度
                        int width = view.getWidth();
                        if (width <= 0) {
                            view.measure(0, 0);
                            width = view.getMeasuredWidth();
                        }
                        int margin = isEqualLength ? (tabViewWidth - width) / 2 : (tabViewWidth - width) / 4;

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.setMargins(margin, 0, margin, 0);
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void reflexMeasure(final TabLayout tabLayout, final boolean isEqualLength) {
        reflexMeasure(tabLayout, 0, isEqualLength);
    }

    /**
     * 重新测量设置TabLayout的tabView的显示,用于设置下划线长度、下划线距离文本、customView距离
     *
     * @param tabLayout      控件
     * @param verticalMargin 底部下划线距离文本、customView距离,或者顶部下划线距离文本、customView距离
     * @param isEqualLength  下划线是否跟文字等长
     */
    public static void reflexMeasure(final TabLayout tabLayout, final int verticalMargin, final boolean isEqualLength) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int tabLayoutHeight = tabLayout.getHeight();
                    if (tabLayoutHeight <= 0) {
                        tabLayoutHeight = tabLayout.getMeasuredHeight();
                    }
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);//拿到27.1.1mTabStrip、28.0.0slidingTabIndicator属性
                    int tabIndicatorGravity = 0;// =0时下划线 指示器，=2时上划线 指示器
                    int selectedIndicatorHeight = 0;// 指示器高度
                    int maxTabLayoutPadding = 0;

                    try {
                        Field tabIndicatorGravityField = tabLayout.getClass().getDeclaredField("tabIndicatorGravity");
                        tabIndicatorGravityField.setAccessible(true);
                        tabIndicatorGravity = (int) tabIndicatorGravityField.get(tabLayout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Field selectedIndicatorHeightField = null;
                    try {
                        selectedIndicatorHeightField = mTabStrip.getClass().getDeclaredField("mSelectedIndicatorHeight");
                    } catch (Exception e) {
                        try {
                            selectedIndicatorHeightField = mTabStrip.getClass().getDeclaredField("selectedIndicatorHeight");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (selectedIndicatorHeightField != null) {
                        selectedIndicatorHeightField.setAccessible(true);
                        selectedIndicatorHeight = (int) selectedIndicatorHeightField.get(mTabStrip);
                    }

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        tabView.setPadding(0, 0, 0, 0);
                        int tabViewWidth = tabView.getWidth();
                        if (tabViewWidth <= 0) {
                            tabView.measure(0, 0);
                            tabViewWidth = tabView.getMeasuredWidth();
                        }
                        Field customViewField = null;
                        try {
                            customViewField = tabView.getClass().getDeclaredField("mCustomView");
                        } catch (Exception e) {
                            try {
                                customViewField = tabView.getClass().getDeclaredField("customView");
                            } catch (Exception e1) {

                            }
                        }
                        View view = null;
                        if (customViewField != null) {
                            customViewField.setAccessible(true);
                            view = (View) customViewField.get(tabView);
                        }
                        if (view == null) {
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
                            view = (TextView) textViewField.get(tabView);
                        }

                        //测量mTextView、mCustomView的宽度
                        int width = view.getWidth();
                        int height = view.getHeight();
                        if (width <= 0 || height <= 0) {
                            view.measure(0, 0);
                            width = view.getMeasuredWidth();
                            height = view.getMeasuredHeight();
                        }
                        int margin = isEqualLength ? (tabViewWidth - width) / 2 : (tabViewWidth - width) / 4;

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        int tempMargin = Math.max(0, (tabLayoutHeight - height) / 2 - selectedIndicatorHeight - Math.abs(verticalMargin));
                        maxTabLayoutPadding = Math.max(tempMargin, maxTabLayoutPadding);
                        params.setMargins(margin, tabIndicatorGravity == 0 ? 0 : -tempMargin, margin, tabIndicatorGravity == 0 ? -tempMargin : 0);
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                    if (maxTabLayoutPadding != 0) {
                        tabLayout.setPadding(tabLayout.getPaddingLeft(), tabIndicatorGravity == 0 ? 0 : maxTabLayoutPadding, tabLayout.getPaddingRight(), tabIndicatorGravity == 0 ? maxTabLayoutPadding : 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置字体粗细
     *
     * @param tabLayout 标签容器
     * @param tab       要设置的tab
     * @param isBold    是否加粗
     */
    public static void setTypeface(TabLayout tabLayout, TabLayout.Tab tab, boolean isBold) {
        try {
            TextView title = (TextView) (((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(tab.getPosition())).getChildAt(1));
            if (title != null) {
                if (isBold) {
                    title.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    title.setTypeface(Typeface.DEFAULT);
                }
            }
        } catch (Exception e) {

        }
    }
}
