package com.hykj.base.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

/**
 * 用于ViewPager+图片预览
 */
public class ZoomImageView extends AppCompatImageView implements View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener, ViewTreeObserver.OnGlobalLayoutListener {

    //初始缩放值
    private float initScale = 1.0f;
    private float mMinScale;
    private float mMaxScale;
    private float ratio = 3;//初始缩放值的倍数,默认3倍

    //灵敏度
    private float mTouchSlop;
    private final Matrix mScaleMatrix = new Matrix();

    //缩放工具类
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    //是否正在缩放
    private boolean isAutoScale;
    //是否在移动
    private boolean isCanDrag = false;

    //触摸点个数，以及触摸坐标
    private int lastPointerCount = 0;
    private float mLastX;
    private float mLastY;

    //单击、长按监听
    private OnClickListener mClickListener;
    private OnLongClickListener mLongClk;


    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);//允许矩阵
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context, onGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {//双击事件
            if (isAutoScale) return true;

            float x = e.getX();
            float y = e.getY();
            float scale = getScale() + 0.01f;
            if (scale <= mMinScale) {
                ZoomImageView.this.postDelayed(new AutoScaleRunnable(mMinScale, x, y), 16);
            } else if (scale > mMinScale && scale < mMaxScale) {
                ZoomImageView.this.postDelayed(new AutoScaleRunnable(mMaxScale, x, y), 16);
            } else {
                ZoomImageView.this.postDelayed(new AutoScaleRunnable(initScale, x, y), 16);
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {//跟onDown组成单击事件
            postDelayed(clickRunnable, 250);
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            removeCallbacks(clickRunnable);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {//长按事件
            if (mLongClk != null) {
                mLongClk.onLongClick(ZoomImageView.this);
            }
        }
    };

    private Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            if (mClickListener != null) {
                mClickListener.onClick(ZoomImageView.this);
            }
        }
    };

    /**
     * 手势缩放
     *
     * @param detector 缩放工具类
     * @return true代表消费事件
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();//当前缩放比例
        float scaleFactor = detector.getScaleFactor();//当前缩放比例/之前的缩放比

        //还未放大到最大，还未缩小到最小
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
            if (scaleFactor * scale > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            /**
             * 设置缩放比例,以触控点为中心
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            //检测缩放、移动是否会产生白边
            checkExceedBorder();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 检测缩放、边缘是否会产生白边
     */
    private void checkExceedBorder() {
        RectF rectF = getMatrixRectF();
        int width = getWidth();
        int height = getHeight();
        //防止白边让View移动的偏移量
        float deltaX = 0.0f;
        float deltaY = 0.0f;

        if (rectF.width() + 0.01 >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        } else {
            deltaX = (width - rectF.right - rectF.left) / 2;
        }
        if (rectF.height() + 0.01 >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        } else {
            deltaY = (height - rectF.bottom - rectF.top) / 2;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event))
            return true;

        //获取触摸点坐标
        int pointerCount = event.getPointerCount();
        float x = 0;
        float y = 0;
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
            lastPointerCount = pointerCount;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isChildrenInterceptTouchEvent();//是否让父类不拦截事件
                break;
            case MotionEvent.ACTION_MOVE:
                isChildrenInterceptTouchEvent();
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                //在移动
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        if (!isParentInterceptTouchEvent(dx,dy)) {//父类不拦截事件
                            RectF rectF = getMatrixRectF();
                            //当图片宽或者高小于屏幕宽或者高
                            if (rectF.width() < getWidth()) {
                                dx = 0;
                            }
                            if (rectF.height() < getHeight()) {
                                dy = 0;
                            }
                            mScaleMatrix.postTranslate(dx, dy);
                            checkExceedBorder();
                            setImageMatrix(mScaleMatrix);
                        }
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    /**
     * 初始化图片宽高，如果图片宽高大于View宽高，则缩小
     */
    @Override
    public void onGlobalLayout() {
        Drawable d = getDrawable();
        if (d == null) return;

        //得到屏幕宽高，图片宽高
        int width = getWidth();
        int height = getHeight();
        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();

        float scale;
        //设置初始缩放值
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        } else if (dh > height && dw <= width) {
            scale = height * 1.0f / dh;
        } else if (dw > width && dh > height) {
            scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        } else {//宽高均小于屏幕宽高,则放大到最小倍数
            scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        }
        initScale = scale;
        mMaxScale = ratio * initScale;
        mMinScale = (float) Math.sqrt(initScale * mMaxScale);
        //将图片移动到屏幕中央
        mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
        mScaleMatrix.postScale(scale, scale, width / 2, height / 2);
        setImageMatrix(mScaleMatrix);

        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * 获取当前缩放比例
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * @return 当前图片在屏幕上的坐标信息
     */
    public RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());//设置图片大小
            mScaleMatrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 是否在移动
     *
     * @param dx 水平移动距离
     * @param dy 垂直移动距离
     * @return true移动 false 点击
     */
    public boolean isCanDrag(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) >= mTouchSlop;
    }

    /**
     * @return 子类是否拦截事件
     */
    public void isChildrenInterceptTouchEvent() {
        RectF rectF = getMatrixRectF();
        if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
            ViewParent parent = getParent();
            while (parent != null && !(parent instanceof ViewPager)) {
                parent = parent.getParent();
            }
            if (parent != null)
                getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * @return 父类是否拦截子类事件
     */
    private boolean isParentInterceptTouchEvent(float dx,float dy) {
        RectF rectF = getMatrixRectF();
        if ((rectF.left == 0 && dx > 0) || (rectF.right == getWidth() && dx < 0) || rectF.width() < getWidth()
                || (rectF.top == 0 && dy > 0) || (rectF.bottom == getWidth() && dy < 0) || rectF.height() < getHeight()) {
            ViewParent parent = getParent();
            while (parent != null && !(parent instanceof ViewPager)) {
                parent = parent.getParent();
            }
            if (parent != null)
                getParent().requestDisallowInterceptTouchEvent(false);
            return parent != null;
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        mClickListener = l;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        mLongClk = l;
    }

    /**
     * 将一次缩放分16毫秒来一段段进行，体验好
     */
    private class AutoScaleRunnable implements Runnable {
        private static final float BIGGER = 1.07f;
        private static final float SMALLER = 0.93f;
        private float tempScale;
        private float mTargetScale;//目标缩放值
        private float x, y;//缩放中心坐标


        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            if (getScale() < mTargetScale) {
                tempScale = BIGGER;
            } else {
                tempScale = SMALLER;
            }
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tempScale, tempScale, x, y);
            checkExceedBorder();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();
            //如果还未放大到最大或者缩小到最小，等待16毫秒继续缩放
            if ((currentScale < mTargetScale && tempScale > 1.0f) || (currentScale > mTargetScale && tempScale < 1.0f)) {
                ZoomImageView.this.postDelayed(this, 16);
            } else {//缩放至目标缩放值
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkExceedBorder();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    public float getMaxScale() {
        return mMaxScale;
    }

    /**
     * @param ratio 倍数
     */
    public void setRatio(float ratio) {
        if (ratio <= 1)
            throw new IllegalArgumentException("ratio it can`t than 1 small");
        this.ratio = ratio;
    }

    /**
     * 重置drawable比例,当当前缩放比例不等于初始缩放比例
     */
    public void resetScale() {
        float scale = getScale();
        if (scale != initScale)
            ZoomImageView.this.postDelayed(new AutoScaleRunnable(initScale, getWidth() / 2, getHeight() / 2), 16);
    }
}
