package com.hykj.base.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 
 * @author zhy
 * 
 */
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration
{

    private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
    private Drawable mDividerV;
    private Drawable mDividerH;
    private int mSizeH;
    private int mSizeV;
    private final Rect mBounds = new Rect();

    public DividerGridItemDecoration(Context context, int dividerH, int dividerV, int sizeH, int sizeV){
        mDividerH = context.getResources().getDrawable(dividerH);
        mDividerV = context.getResources().getDrawable(dividerV);
        mSizeH = sizeH;
        mSizeV = sizeV;
    }
    public DividerGridItemDecoration(Context context)
    {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDividerH = a.getDrawable(0);
        mDividerV = a.getDrawable(0);
        mSizeH = mDividerH.getIntrinsicHeight();
        mSizeV = mDividerV.getIntrinsicWidth();
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state)
    {

        drawHorizontal(c, parent);
        drawVertical(c, parent);

    }

    private int getSpanCount(RecyclerView parent)
    {
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
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int right = (int) (mBounds.right + Math.abs(child.getTranslationX()));
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
            c.clipRect(left, parent.getPaddingTop(), right, parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = (int) (mBounds.bottom + Math.abs(child.getTranslationY()));
            int top = bottom - mDividerV.getIntrinsicHeight();
            mDividerV.setBounds(left, top, right, bottom);
            mDividerV.draw(c);
        }
        c.restore();
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
            int childCount)
    {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else
            {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
            int childCount)
    {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
            RecyclerView parent)
    {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
        {
            outRect.set(0, 0, mSizeH, 0);
        } else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
        {
            outRect.set(0, 0, 0, mSizeV);
        } else {
            outRect.set(0, 0, mSizeH, mSizeV);
        }
    }
}