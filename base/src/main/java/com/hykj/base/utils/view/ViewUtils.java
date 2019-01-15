package com.hykj.base.utils.view;

import android.view.View;
import android.view.ViewGroup;

/**
 * View工具类
 */
public class ViewUtils {

    /**
     * 设置选中
     *
     * @param view
     */
    public static void setChildSelect(View view, boolean isSelected) {
        view.setSelected(isSelected);
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                setChildSelect(parent.getChildAt(i), isSelected);
            }
        }
    }

    /**
     * 设置活动状态
     *
     * @param view
     */
    public static void setChildActivated(View view, boolean activated) {
        view.setActivated(activated);
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                setChildActivated(parent.getChildAt(i), activated);
            }
        }
    }
}
