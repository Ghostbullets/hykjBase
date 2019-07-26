package com.hykj.base.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 基础类
 */
public class BasePopupWindow extends PopupWindow {
    protected boolean isSetShadows = true;//是否设置阴影
    protected Context context;

    public BasePopupWindow(Context context) {
        this(context,null);
    }

    public BasePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (isSetShadows)
            showOrHideShade(true);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        if (isSetShadows)
            showOrHideShade(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        showOrHideShade(false);
    }

    /**
     * 显示隐藏阴影
     *
     * @param isShow 是否显示阴影
     */
    private void showOrHideShade(boolean isShow) {
        if (context instanceof Activity) {
            WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
            lp.alpha = isShow ? 0.7f : 1.0f;
            ((Activity) context).getWindow().setAttributes(lp);
        }
    }

    public void setSetShadows(boolean setShadows) {
        isSetShadows = setShadows;
    }

    public boolean isSetShadows() {
        return isSetShadows;
    }
}
