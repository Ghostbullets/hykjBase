package com.hykj.base.adapter;

import android.support.annotation.LayoutRes;

public class LayoutItem {
    private int type;
    private @LayoutRes
    int layoutId;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLayoutId() {
        return this.layoutId;
    }

    public void setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public LayoutItem(int type, @LayoutRes int layoutId) {
        this.type = type;
        this.layoutId = layoutId;
    }
}
