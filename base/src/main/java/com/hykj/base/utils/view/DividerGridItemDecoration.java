package com.hykj.base.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * @author cjf
 * 分割线原理：getItemOffsets()方法里面调用 outRect.set(0, 0, mSizeH, mSizeV);我个人理解是设置了item的margin，比如这里设置item的右外边距、底外边距
 * 这时候其实已经相当于设置了一个分割线，颜色是透明的，RecycleView是什么颜色，则item之间的分割就是什么颜色
 * 这个时候使用 parent.getDecoratedBoundsWithMargins(child, mBounds)，获取到item的矩阵大小，这里面就包括上面设置的margin，
 * mBounds.right得到分割线的right，减去mDividerH.getIntrinsicWidth()，得到分割线的left，然后 mDividerH.setBounds(left, 0, right, RecycleView.getHeight());
 * 即可设置出一条竖直的有颜色的分割线，分割线宽度=mDividerH.getIntrinsicWidth()
 */
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};//得到的高度跟宽度都是3
    private Drawable mDividerV;
    private Drawable mDividerH;
    private int mSizeH;
    private int mSizeV;
    private final Rect mBounds = new Rect();

    public DividerGridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDividerH = a.getDrawable(0);
        mDividerV = a.getDrawable(0);
        mSizeH = mDividerH.getIntrinsicHeight();
        mSizeV = mDividerV.getIntrinsicWidth();
        a.recycle();
    }

    public DividerGridItemDecoration(Context context, @DrawableRes int dividerH, @DrawableRes int dividerV) {
        mDividerH = context.getResources().getDrawable(dividerH);
        mDividerV = context.getResources().getDrawable(dividerV);
        mSizeH = mDividerH.getIntrinsicWidth();
        mSizeV = mDividerV.getIntrinsicHeight();
        init(context, dividerH, dividerV, 0, 0);
    }

    public DividerGridItemDecoration(Context context, @DrawableRes int dividerH, @DrawableRes int dividerV, int sizeH, int sizeV) {
        init(context, dividerH, dividerV, sizeH, sizeV);
    }

    private void init(Context context, @DrawableRes int dividerH, @DrawableRes int dividerV, int sizeH, int sizeV) {
        mDividerH = context.getResources().getDrawable(dividerH);
        mDividerV = context.getResources().getDrawable(dividerV);
        if (sizeH == 0)
            sizeH = mDividerH.getIntrinsicWidth();
        if (sizeV == 0)
            sizeV = mDividerV.getIntrinsicHeight();
        this.mSizeH = sizeH;
        this.mSizeV = sizeV;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        c.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight();
            c.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (isLastColumn(parent, parent.getChildAdapterPosition(child), getSpanCount(parent), parent.getChildCount())) {
                continue;
            }
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int right = mBounds.right + Math.round(child.getTranslationX());
            int left = right - mDividerH.getIntrinsicWidth();
            mDividerH.setBounds(left, top, right, bottom);
            mDividerH.draw(c);
        }
        c.restore();
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        c.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            c.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (isLastRow(parent, parent.getChildAdapterPosition(child), getSpanCount(parent), parent.getChildCount())) {
                continue;
            }
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - mDividerV.getIntrinsicHeight();
            mDividerV.setBounds(left, top, right, bottom);
            mDividerV.draw(c);
        }
        c.restore();
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) // 如果是最后一列，则不需要绘制右边
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) // 如果是最后一列，则不需要绘制右边
                    return true;
            } else {
                childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else { // StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                    return true;
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
                               RecyclerView parent) {

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        int spanCount = getSpanCount(parent);
        if (parent.getAdapter() != null) {
            int childCount = parent.getAdapter().getItemCount();
            //是最后一行，又是最后一列，则不绘制
            if (isLastRow(parent, itemPosition, spanCount, childCount) && isLastColumn(parent, itemPosition, spanCount, childCount)) {
                outRect.set(0, 0, 0, 0);
            } else if (isLastRow(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            {
                outRect.set(0, 0, mSizeH, 0);
            } else if (isLastColumn(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
            {
                outRect.set(0, 0, 0, mSizeV);
            } else {
                outRect.set(0, 0, mSizeH, mSizeV);
            }
        }
    }
}