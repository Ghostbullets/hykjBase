package com.hykj.base.adapter;

public class LayoutItem {
    int type;
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

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public LayoutItem(int type, int layoutId) {
        this.type = type;
        this.layoutId = layoutId;
    }
}
