package com.hykj.base.utils.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

/**
 * 发现折叠+切换Fragment偶尔出现AppBarLayout卡住，无法滑动问题，只有ViewPager的RecyclerView部分可以滑动
 *
 * 以下代码可以解决问题
 */
public class FixCanDragAppBarLayoutBehavior extends FixAppBarLayoutBehavior {
    public FixCanDragAppBarLayoutBehavior() {
    }

    public FixCanDragAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDragCallback(new DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return true;
            }
        });
    }
}
