package com.hykj.base.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 显示尺寸类
 */
public class DisplayUtils {
    private DisplayMetrics mMetrics;
    // 密度
    float mDensity;
    //缩放密度
    float mScaledDensity;

    public DisplayUtils() {
        init(ContextKeep.getContext());
    }

    public DisplayUtils(Context context) {
        init(context);
    }

    public void init(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mMetrics);
        mDensity = mMetrics.density;
        mScaledDensity = mMetrics.scaledDensity;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * dp转像素
     *
     * @param dp
     * @return
     */
    public int dp2px(int dp) {
        return (int) (dp * mDensity + 0.5f);
    }

    /**
     * 像素转DP
     *
     * @param px
     * @return
     */
    public int px2dp(int px) {
        return (int) (px / mDensity + 0.5f);
    }

    public int sp2px(int sp) {
        return (int) (sp * mScaledDensity + 0.5f);
    }

    /**
     * 屏幕宽度
     *
     * @return
     */
    public int screenWidth() {
        return mMetrics.widthPixels;
    }

    /**
     * 屏幕高度
     *
     * @return
     */
    public int screenHeight() {
        return mMetrics.heightPixels;
    }

    /**
     * 尺寸转px
     *
     * @param context 上下文
     * @param unit    尺寸单位
     * @param size    尺寸大小
     * @return
     */
    public static int size2px(Context context, int unit, int size) {
        return (int) TypedValue.applyDimension(unit, size, context.getResources().getDisplayMetrics());
    }

    public static int size2px( int unit, int size) {
        return (int) TypedValue.applyDimension(unit, size, ContextKeep.getContext().getResources().getDisplayMetrics());
    }

    /**
     * 得到状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //得到状态栏的资源标识符id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置状态栏透明
     *
     * @param context
     * @param view    View
     * @return 返回状态栏高度
     */
    public static int setStatusBarHeight(Context context, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getStatusBarHeight(context);
        view.setLayoutParams(params);
        return params.height;
    }

}
