package com.hykj.base.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * RecycleView监听底部事件
 */
public class OnScrollListenerTag extends RecyclerView.OnScrollListener {
    private ScrollCallback callback;
    private boolean isRefreshOnTop;//是否在滑动到第一个item时刷新适配器

    public OnScrollListenerTag(ScrollCallback callback) {
        this.callback = callback;
    }

    public OnScrollListenerTag(boolean isRefreshOnTop, ScrollCallback callback) {
        this.callback = callback;
        this.isRefreshOnTop = isRefreshOnTop;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastPosition = -1;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            lastPosition = ((GridLayoutManager) manager).findLastVisibleItemPosition();

        } else if (manager instanceof LinearLayoutManager) {
            lastPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();

        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(null);
            lastPosition = lastPositions[layoutManager.getSpanCount() - 1];

            int[] firstVisibleItem = layoutManager.findFirstVisibleItemPositions(null);
            boolean firstItemIsZero = false;
            for (int value : firstVisibleItem) {
                if (value < layoutManager.getSpanCount()) {
                    firstItemIsZero = true;
                    break;
                }
            }
            if (isRefreshOnTop && firstItemIsZero) {
                //调用requestLayout方法只会执行onMeasure方法和onLayout方法，并不会执行onDraw方法；调用invalidate方法只会执行onDraw方法
                layoutManager.invalidateSpanAssignments();
            }
        }
        if (lastPosition + 5 >= recyclerView.getLayoutManager().getItemCount() - 1) {
            callback.scrollEnd();
        }
    }
}
