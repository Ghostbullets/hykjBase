package com.hykj.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * 基础对象
 *
 * @author LZR 2016年10月5日
 * @version 1.0
 */
public class CustomBaseDialog extends Dialog {

	protected Object tag;

	protected CustomBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	protected CustomBaseDialog(Context context, int theme) {
		super(context, theme);
	}

	protected CustomBaseDialog(Context context) {
		super(context);
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	/**
	 * 点击确定按钮监听
	 */
	public interface OnCommitClickListener {
		void onClick(View v, CustomBaseDialog dialog);
	}

	/**
	 * 弹窗关闭监听
	 */
	public interface OnDialogDismissListener {
		void onDialogDismiss(CustomBaseDialog dialog);
	}

}
