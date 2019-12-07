package com.hykj.base.view.fitsystem;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * created by cjf
 * on: 2019/10/26
 * 解决 加入android:fitsSystemWindows="true"后，解决了输入法遮挡了输入框的问题，但是界面顶部出现了状态栏高度的白条。
 */
public class FitSystemFrameLayout extends FrameLayout {
    public FitSystemFrameLayout(Context context) {
        super(context);
    }

    public FitSystemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitSystemFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}
