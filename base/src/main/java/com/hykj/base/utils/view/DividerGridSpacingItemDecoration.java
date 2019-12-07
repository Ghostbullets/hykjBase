package com.hykj.base.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * @author cjf
 * 分割线原理：
 * RecyclerView 中的 ItemView 外面会包裹着一个矩形（outRect）
 * getItemOffsets()方法设置ItemView的内嵌偏移长度（inset）
 * 内嵌偏移长度 是指：该矩形（outRect）与 ItemView的间隔
 * 影响着ItemView的Padding值，默认outRect.set(0, 0, 0,0)，即矩形和Item重叠，所以看起来矩形就消失了
 * 如果outRect.set(0, 0, mSizeH, mSizeV),这会使得ItemView跟矩形右边、底部有距离，即设置了padding(注:默认itemView填充满矩形，所以设置了这个值会让item变小)
 * 如果itemView的宽高设置为固定值，则会使得itemView内部东西往里面挤压，比如TextView的文字会有部分看不到，这个自己测试一下就很容易明白了。
 * 这时候其实已经相当于设置了一个分割线，颜色是透明的，RecycleView是什么颜色，则item之间的分割就是什么颜色。分割线·其实说白了，就是ItemView外部跟矩形内部,这之间的区域
 * <p>
 * 这个时候使用 parent.getDecoratedBoundsWithMargins(child, mBounds)，获取到的是item的矩阵大小+item的margin所构成的矩形
 * mBounds.right得到分割线的right，减去mSizeH，得到分割线的left，然后 mDividerH.setBounds(left, 0, right, RecycleView.getHeight());
 * 即可设置出一条竖直的有颜色的分割线，分割线宽度=mSizeH
 * <p>
 * 结论：outRect4个属性值影响着ItemView的Padding值
 * 具体过程：在RecyclerView进行子View宽高测量时（measureChild（）），会将getItemOffsets（）里设置的 outRect4个属性值
 * （Top、Bottom、Left、Right）通过insert值累加 ，并最终添加到子View的 Padding属性中
 */
public class DividerGridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};//得到的高度跟宽度都是3
    private Drawable mDividerV;
    private Drawable mDividerH;
    private int mSizeH;
    private int mSizeV;
    private final Rect mBounds = new Rect();

    public DividerGridSpacingItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDividerH = a.getDrawable(0);
        mDividerV = a.getDrawable(0);
        mSizeH = mDividerH.getIntrinsicWidth();
        mSizeV = mDividerV.getIntrinsicHeight();
        a.recycle();
    }

    public DividerGridSpacingItemDecoration(Context context, @DrawableRes int dividerH, @DrawableRes int dividerV) {
        init(context, dividerH, dividerV, 0, 0);
    }

    public DividerGridSpacingItemDecoration(Context context, @DrawableRes int dividerH, @DrawableRes int dividerV, int sizeH, int sizeV) {
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
        if (parent.getAdapter() == null || parent.getChildCount() == 0)
            return;
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        drawHorizontal(c, parent, spanCount, childCount);
        drawVertical(c, parent, spanCount, childCount);
    }

    /**
     * 返回RecyclerView的LayoutManager的SpanCount
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    /**
     * 返回RecyclerView的LayoutManager的布局类型
     *
     * @param parent
     * @return
     */
    public int getOrientation(RecyclerView parent) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }
        return GridLayoutManager.VERTICAL;
    }

    /**
     * 返回要绘制分割线的item的总数
     *
     * @param isTrueOrientation
     * @param parent
     * @param spanCount
     * @param childCount
     * @return
     */
    public int getDrawCount(boolean isTrueOrientation, RecyclerView parent, int spanCount, int childCount) {
        int pos = parent.getChildAdapterPosition(parent.getChildAt(parent.getChildCount() - 1));//得到最后一个childView的pos
        int offset = childCount % spanCount == 0 ? spanCount : childCount % spanCount;//获取偏差
        boolean isLast = (pos + offset) >= childCount;//显示在“屏幕”上的最后一个行、列是不是“所有数据”里面的最后一行、列了
        return isTrueOrientation && isLast ? parent.getChildCount() - offset : parent.getChildCount();
    }

    //绘制水平分割线，注意，线是竖直的
    public void drawHorizontal(Canvas c, RecyclerView parent, int spanCount, int childCount) {
        c.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            c.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }
        //当布局方向是水平的时候，除了最后一列以外，其他的列都需要绘制位于它们右边的分割线
        //当布局方向是垂直的时候，则所有的列都要绘制分割线，只不过第一列左边绘制宽度是0,最后一列右边绘制宽度是0
        boolean isHorizontal = getOrientation(parent) == GridLayoutManager.HORIZONTAL;
        int size = getDrawCount(isHorizontal, parent, spanCount, childCount);
        for (int i = 0; i < size; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);

            if (isHorizontal) {//水平布局情况下，只需要绘制item的右边
                int right = mBounds.right + Math.round(child.getTranslationX());
                int left = right - mSizeH;
                mDividerH.setBounds(left, top, right, bottom);
                mDividerH.draw(c);
            } else {//垂直布局情况下，需要绘制item的右边、左边
                //绘制item右边分割线
                int right = mBounds.right + Math.round(child.getTranslationX());
                int left = child.getRight() + Math.round(child.getTranslationX());
                mDividerH.setBounds(left, top, right, bottom);
                mDividerH.draw(c);

                //绘制item左边分割线
                left = mBounds.left + Math.round(child.getTranslationX());
                right = child.getLeft() + Math.round(child.getTranslationX());
                mDividerH.setBounds(left, top, right, bottom);
                mDividerH.draw(c);
            }
        }
        c.restore();
    }

    //绘制垂直分割线，注意，线是横向的
    public void drawVertical(Canvas c, RecyclerView parent, int spanCount, int childCount) {
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
        //当布局方向是垂直的时候，除了最后一行以外，其他的行都需要绘制位于它们下边的分割线
        //当布局方向是水平的时候，则所有的行都要绘制分割线，只不过第一行上边绘制高度是0,最后一行下边绘制高度是0
        boolean isVertical = getOrientation(parent) == GridLayoutManager.VERTICAL;
        int size = getDrawCount(isVertical, parent, spanCount, childCount);
        for (int i = 0; i < size; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);

            if (isVertical) {//垂直布局情况下，只需要绘制item的底部
                int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                int top = bottom - mSizeV;
                mDividerV.setBounds(left, top, right, bottom);
                mDividerV.draw(c);
            } else {//水平布局情况下，需要绘制item的底部、顶部
                //绘制item底部分割线
                int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                int top = child.getBottom() + Math.round(child.getTranslationY());
                mDividerV.setBounds(left, top, right, bottom);
                mDividerV.draw(c);

                //绘制item顶部分割线
                top = mBounds.top + Math.round(child.getTranslationY());
                bottom = child.getTop() + Math.round(child.getTranslationY());
                mDividerV.setBounds(left, top, right, bottom);
                mDividerV.draw(c);
            }
        }
        c.restore();
    }

    //是否是最后一列
    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == GridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)
                    return true;
            } else {
                childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
                if (pos >= childCount)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)
                    return true;
            } else {
                childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
                if (pos >= childCount)
                    return true;
            }
        }
        return false;
    }

    //是否是最后一行
    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int orientation = ((GridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == GridLayoutManager.VERTICAL) {
                childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
                if (pos >= childCount)
                    return true;
            } else {
                if ((pos + 1) % spanCount == 0)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
                if (pos >= childCount)
                    return true;
            } else { // StaggeredGridLayoutManager 且横向滚动
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
            int column = itemPosition % spanCount;
            int left = 0;
            int right = 0;
            int top = 0;
            int bottom = 0;
            if (getOrientation(parent) == GridLayoutManager.VERTICAL) {//垂直布局
                //同一个item的left+right=mSizeH*(spanCount-1)/spanCount;两个相邻的item的left+right=mSizeH
                left = column * mSizeH / spanCount;
                right = mSizeH - (column + 1) * mSizeH / spanCount;
                bottom = mSizeV;
                if (isLastRow(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
                    bottom = 0;
            } else {//水平布局
                top = column * mSizeV / spanCount;
                bottom = mSizeV - (column + 1) * mSizeV / spanCount;
                right = mSizeH;
                if (isLastColumn(parent, itemPosition, spanCount, childCount))//如果是最后一列，则不需要绘制右边
                    right = 0;
            }
            outRect.set(left, top, right, bottom);
        }
    }
}