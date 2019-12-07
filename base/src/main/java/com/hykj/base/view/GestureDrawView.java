package com.hykj.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hykj.base.R;


/**
 * 手势轨迹View
 */
public class GestureDrawView extends View implements View.OnTouchListener {
    private Canvas canvas;
    private Bitmap bitmap;//存放绘制的内容的位图
    private Path path;//路径
    private Paint paint;//画笔
    private int strokeWidth = 8;//画笔粗细
    private @ColorInt
    int color = Color.BLACK;//画笔颜色
    private int width, height;//控件宽高

    public GestureDrawView(Context context) {
        this(context, null);
    }

    public GestureDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.GestureDrawView);
            strokeWidth = t.getInt(R.styleable.GestureDrawView_gdvStrokeWidth, strokeWidth);
            color = t.getColor(R.styleable.GestureDrawView_gdvColor, Color.BLACK);
            t.recycle();
        }
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        path = new Path();
        setOnTouchListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        resetDraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
        this.canvas.drawPath(path, paint);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = Math.min(Math.max(getPaddingLeft(), event.getX()), width - getPaddingRight());
        float y = Math.min(Math.max(getPaddingTop(), event.getY()), height - getPaddingBottom());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://当手指按下的时候触发
                path.moveTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE://当手指移动的时候触发
                path.lineTo(x, y);
                invalidate();
                break;
        }
        return true;
    }

    //得到绘制的位图
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * 清空绘制
     */
    public void clear() {
        resetDraw();
        invalidate();
    }

    //重置绘制信息
    private void resetDraw() {
        path.reset();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }
}
