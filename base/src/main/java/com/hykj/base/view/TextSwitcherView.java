package com.hykj.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hykj.base.R;
import com.hykj.base.utils.DisplayUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * created by cjf
 * on: 2019/7/29
 * 自定义走马灯
 * <com.hykj.base.view.TextSwitcherView
 * android:id="@+id/text_switcher_view"
 * android:layout_width="50dp"
 * android:layout_height="wrap_content"
 * android:visibility="gone"
 * app:tsvSingleLine="true"
 * app:tsvDelayTime="3000"
 * app:tsvDuration="1000"
 * app:tsvGravity="center"
 * app:tsvTextColor="@android:color/white"
 * app:tsvTextSize="12sp"
 * app:tsvIsMaximumTextWidth="true"
 * app:tsvDirection="left_to_right" />
 */
public class TextSwitcherView<T> extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private boolean isFirst = true;
    private int index = 0;//上下滚动下标
    private Handler handler = new Handler();
    private boolean isFlipping = false;//是否启动轮播
    private boolean isMaximumTextWidth;//是否取文本最长的一个作为宽度,建议在设置控件宽度为WRAP时设置该属性为true
    private boolean isCustomView;//是否自定义布局，不使用默认的TextView
    private List<T> dataList = new ArrayList<>();//数据

    private int duration = 1000;//滚动切换所需时间
    private int delayTime = 3000;//间隔多久滚动切换
    private int textSize;//文字大小
    private @ColorInt
    int textColor = Color.WHITE;//文字颜色
    private int gravity = Gravity.CENTER;//文字位置
    private boolean singleLine = true;//单行设置
    private @Direction
    int direction = Direction.bottom_to_top;//动画方向
    private TextView textView;//默认填充的TextView

    public TextSwitcherView(Context context) {
        this(context, null);
    }

    public TextSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TextSwitcherView);
            singleLine = t.getBoolean(R.styleable.TextSwitcherView_tsvSingleLine, true);
            gravity = t.getInt(R.styleable.TextSwitcherView_tsvGravity, Gravity.CENTER);
            textColor = t.getColor(R.styleable.TextSwitcherView_tsvTextColor, Color.WHITE);
            textSize = t.getDimensionPixelSize(R.styleable.TextSwitcherView_tsvTextSize, sp2px(12));
            duration = t.getInt(R.styleable.TextSwitcherView_tsvDuration, 1000);
            delayTime = t.getInt(R.styleable.TextSwitcherView_tsvDelayTime, 3000);
            direction = t.getInt(R.styleable.TextSwitcherView_tsvDirection, Direction.bottom_to_top);
            isMaximumTextWidth = t.getBoolean(R.styleable.TextSwitcherView_tsvIsMaximumTextWidth, false);
            t.recycle();
        } else {
            textSize = sp2px(12);
        }

        TranslateAnimation inAnimation;
        TranslateAnimation outAnimation;
        switch (direction) {
            case Direction.left_to_right:
            case Direction.right_to_left:
                boolean isTurnLeft = direction == Direction.right_to_left;//动画是否向左
                inAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, isTurnLeft ? 1f : -1f, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
                outAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, isTurnLeft ? -1f : 1f,
                        Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
                break;
            case Direction.bottom_to_top:
            case Direction.top_to_bottom:
            default:
                boolean isUp = direction == Direction.bottom_to_top;//动画是否向上
                inAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, isUp ? 1f : -1f, Animation.RELATIVE_TO_PARENT, 0f);
                outAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, isUp ? -1f : 1f);
                break;
        }
        inAnimation.setDuration(duration);
        outAnimation.setDuration(duration);
        setInAnimation(inAnimation);
        setOutAnimation(outAnimation);
        setFactory(this);
    }

    @Override
    public void setFactory(ViewFactory factory) {
        super.setFactory(factory);
        isCustomView = factory != this;
    }

    @Override
    public View makeView() {
        textView = new TextView(getContext());
        if (singleLine)
            textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        if (!isMaximumTextWidth)
            textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(gravity);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = gravity;
        textView.setLayoutParams(params);
        return textView;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isFlipping)
                return;
            index = (index + 1) % dataList.size();
            setText(dataList.get(index).toString());
            startFlipping();
        }
    };

    /**
     * 开启信息轮播 在{@link Activity#onResume()} 时调用,也可以主动调用
     *
     * @return
     */
    public TextSwitcherView<T> startFlipping() {
        if (dataList.size() > 1) {
            handler.removeCallbacks(runnable);
            isFlipping = true;
            handler.postDelayed(runnable, delayTime);
        }
        return this;
    }

    /**
     * 关闭信息轮播    在{@link Activity#onPause()}  时调用
     *
     * @return
     */
    public TextSwitcherView<T> stopFlipping() {
        if (dataList.size() > 1) {
            isFlipping = false;
            handler.removeCallbacks(runnable);
        }
        return this;
    }

    //设置数据
    public TextSwitcherView<T> reloadListView(List<T> list, boolean isClear) {
        stopFlipping();
        if (isClear)
            this.dataList.clear();
        if (list != null)
            this.dataList.addAll(list);
        measureWidth();
        return this;
    }

    //重新测量宽度
    private void measureWidth() {
        //要么是第一次，并且控件宽度为WRAP，则测量；要么是设置了isMaximumTextWidth=true，并且不自定义布局，则绘制
        if ((isFirst && getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) || (isMaximumTextWidth && !isCustomView)) {
            isFirst = false;
            TextPaint paint = textView.getPaint();
            Rect bounds = new Rect();
            int maxWidth = -1;
            for (T t : dataList) {
                String text = t.toString();
                paint.getTextBounds(text, 0, text.length(), bounds);
                maxWidth = Math.max(bounds.width(), maxWidth);
            }
            if (maxWidth != -1) {
                int width = maxWidth + getPaddingLeft() + getPaddingRight() + 5;
                int screenWidth = new DisplayUtils(getContext()).screenWidth();
                getLayoutParams().width = Math.min(screenWidth, width);
            }
        }
    }

    private int sp2px(float textSize) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getContext().getResources().getDisplayMetrics());
    }

    public TextView getTextView() {
        return textView;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Direction.bottom_to_top, Direction.top_to_bottom, Direction.right_to_left, Direction.left_to_right})
    public @interface Direction {
        int bottom_to_top = 0;
        int top_to_bottom = 1;
        int right_to_left = 2;
        int left_to_right = 3;
    }
}
