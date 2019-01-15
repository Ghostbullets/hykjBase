package com.hykj.base.dialog.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单组
 * 
 * @author LZR 2016年7月6日
 * @version 1.0
 */
public class MenuGroup {
	List<MenuItem> items = new ArrayList<>();
	int lastSelect = -1;

	public MenuGroup() {

	}

	public void addMenu(MenuItem item) {
		items.add(item);
	}

	public void clear() {
		items.clear();
	}

	/**
	 * 设置选中项
	 * 
	 * @param position
	 * @return
	 */
	public boolean selectItem(int position) {
		if (position < 0 || position >= items.size())
			return false;
		items.get(position).setSelected(true);

		if (lastSelect != -1 && lastSelect != position)
			items.get(lastSelect).setSelected(false);

		lastSelect = position;
		return true;
	}

	/**
	 * 获取选中项
	 * 
	 * @return
	 */
	public int getSelectPositon() {
		return lastSelect;
	}

	public MenuItem getItem(int position) {
		if (position < 0 || position >= items.size())
			return null;
		return items.get(position);
	}

	public List<MenuItem> getItems() {
		return items;
	}

	public void setItems(List<MenuItem> items) {
		this.items = items;
	}
}
