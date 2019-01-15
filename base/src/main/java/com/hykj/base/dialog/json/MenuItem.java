package com.hykj.base.dialog.json;

/**
 * 菜单项
 * 
 * @author LZR 2016年7月6日
 * @version 1.0
 */
public class MenuItem {
	private boolean selected = false;
	private CharSequence name = "";
	private Object tag;

	public MenuItem() {
		this(false, "", null);
	}

	public MenuItem(CharSequence name, Object tag) {
		this(false, name, tag);
	}

	public MenuItem(boolean selected, CharSequence name, Object tag) {
		this.selected = selected;
		this.name = name;
		this.tag = tag;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public CharSequence getName() {
		return name;
	}

	public void setName(CharSequence name) {
		this.name = name;
	}

	public Object getTag() {
		return tag;
	}
}
