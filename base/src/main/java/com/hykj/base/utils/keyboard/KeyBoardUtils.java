package com.hykj.base.utils.keyboard;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * created by cjf
 * on: 2019/9/3
 */
public class KeyBoardUtils {
    /***
     * 关软件盘  调用该方法传入一个view
     *
     * @param view
     */
    public static void HideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软件盘
     */
    public static void ShowKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
