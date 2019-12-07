package com.hykj.base.utils.view;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.OverScroller;

import java.lang.reflect.Field;

/**
 * 重写 AppBarLayout.Behavior 打印 log，发现在快速滑动到顶部和底部之后，AppBarLayout
 * 在一段时间内还处于 Fling 状态，那么我们想办法把这段无效的 Fling 干掉就好了。
 * 最后翻找 google 的时候发现这是 google 在修复上个版本嵌套滑动的时候引进来的新 bug。。。
 */
public class FixAppBarLayoutBehavior extends AppBarLayout.Behavior {
    private OverScroller scroller;

    public FixAppBarLayoutBehavior() {
        super();
    }

    public FixAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        getParentScroller(context);
    }

    private void getParentScroller(Context context) {
        if (scroller != null) return;
        scroller = new OverScroller(context);
        try {
            Class<?> reflexClass = getClass().getSuperclass().getSuperclass().getSuperclass();//得到当前类的父类的父类，即HeaderBehavior
            Field fieldScroller = reflexClass.getDeclaredField("scroller");
            fieldScroller.setAccessible(true);
            fieldScroller.set(this, scroller);
        } catch (Exception e) {
        }
    }

    //fling上滑appbar然后迅速fling下滑recycler时, HeaderBehavior的mScroller并未停止, 会导致上下来回晃动
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, final View target, int dx, int dy, int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        stopNestedScrollIfNeeded(dy, child, target, type);
        if (scroller != null) { //当recyclerView 做好滑动准备的时候 直接干掉Appbar的滑动
            if (scroller.computeScrollOffset()) scroller.abortAnimation();
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        stopNestedScrollIfNeeded(dyUnconsumed, child, target, type);
    }

    private void stopNestedScrollIfNeeded(int dy, AppBarLayout child, View target, int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {//fling这个场景之下，非用户手势
            final int currOffset = getTopAndBottomOffset();
            if ((dy < 0 && currOffset == 0)//向下滑且已到顶
                    || (dy > 0 && currOffset == -child.getTotalScrollRange())) {//向上滑且已到底
                ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
            }
        }
    }
}
