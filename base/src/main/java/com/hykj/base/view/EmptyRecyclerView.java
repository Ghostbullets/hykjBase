package com.hykj.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义空布局RecycleView
 * 当使用NestedScrollView嵌套ViewPager，ViewPager里面是fragment，fragment里面有RecycleView的时候，
 * 如果对RecycleView使用notifyItemChanged之类的方法时，有可能会导致RecycleView高度变成0，也就会产生一种item数据消失的现象
 */

public class EmptyRecyclerView extends RecyclerView {
    private View emptyView;
    private boolean isShowEmptyRv;//是否在数据为空时还显示recycleView
    private static final String TAG = "EmptyRecyclerView";

    final private AdapterDataObserver observer = new AdapterDataObserver() {//监听数据的变化，执行checkIfEmpty();
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {//当该空布局存在，且存在适配器，执行
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(!isShowEmptyRv && emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);//移除旧监听
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);//注册监听数据刷新
        }
        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    //是否存在空布局
    public boolean isNoEmptyView() {
        return emptyView == null;
    }


    public void setShowEmptyRv(boolean showEmptyRv) {
        isShowEmptyRv = showEmptyRv;
    }
}
