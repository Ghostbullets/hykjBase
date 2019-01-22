package com.hykj.base.bean;

public class TabInfo {
    public int position;
    public String name;
    public boolean isSelected;
    public Object tag;

    public TabInfo(int position, String name, boolean isSelected) {
        this.position = position;
        this.name = name;
        this.isSelected = isSelected;
    }

    public TabInfo(int position, String name, boolean isSelected, Object tag) {
        this.position = position;
        this.name = name;
        this.isSelected = isSelected;
        this.tag = tag;
    }
}
