package com.hykj.base.bean;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

/**
 * 标签类
 */
public class LabelInfo {
    public Class<? extends Fragment> cls;
    public int index;
    public boolean isSelected;
    public @DrawableRes
    int resId;
    public String name;

    public LabelInfo(Class<? extends Fragment> cls, int index, boolean isSelected) {
        this.cls = cls;
        this.index = index;
        this.isSelected = isSelected;
    }

    public LabelInfo(Class<? extends Fragment> cls, int index, boolean isSelected, int drawableResId, String name) {
        this.cls = cls;
        this.index = index;
        this.isSelected = isSelected;
        this.resId = drawableResId;
        this.name = name;
    }
}
