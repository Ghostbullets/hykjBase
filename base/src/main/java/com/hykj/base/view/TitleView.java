package com.hykj.base.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.utils.DisplayUtils;


/**
 * 标题栏
 */
public class TitleView extends LinearLayout {
    private BackClickListener backClickListener;
    private View content;
    private LinearLayout layoutLeft;
    private LinearLayout layoutRight;
    private LinearLayout layoutMiddle;
    private ImageView ivBack;
    private TextView tvTitle;
    private View vTransStatusBar;

    private int lastMiddlePadding = 0;

    public TitleView(Context context) {
        this(context,null);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        content = LayoutInflater.from(context).inflate(R.layout.layout_title_view, this, true);
        layoutLeft = (LinearLayout) findViewById(R.id.layout_title_left);
        layoutMiddle = (LinearLayout) findViewById(R.id.layout_title_middle);
        layoutRight = (LinearLayout) findViewById(R.id.layout_title_right);
        vTransStatusBar = findViewById(R.id.v_status_bar);

        ivBack = (ImageView) findViewById(R.id.iv_title_back);
        tvTitle = (TextView) findViewById(R.id.tv_title_middle);
        tvTitle.setSelected(true);

        ivBack.setImageResource(R.drawable.ic_black_back);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backClickListener != null)
                    backClickListener.onBackClick(view);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int left = layoutLeft.getWidth();
        int right = layoutRight.getWidth();
        int padding = Math.max(left, right);
        padding += 10;
        if (padding != lastMiddlePadding) {
            lastMiddlePadding = padding;
            layoutMiddle.setPadding(lastMiddlePadding, 0, lastMiddlePadding, 0);
        }
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public LinearLayout getLayoutLeft() {
        return layoutLeft;
    }

    public LinearLayout getLayoutRight() {
        return layoutRight;
    }

    public LinearLayout getLayoutMiddle() {
        return layoutMiddle;
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public interface BackClickListener {
        void onBackClick(View v);
    }

    public void setBackClickListener(BackClickListener backClickListener) {
        this.backClickListener = backClickListener;
    }

    /**
     * 设置标题栏半透明
     */
    public void setTranslucentStatus(boolean on) {
        int statusBarHeight = 0;
        if (on)
            statusBarHeight = DisplayUtils.getStatusBarHeight(getContext());
        ViewGroup.LayoutParams params = vTransStatusBar.getLayoutParams();
        params.height = statusBarHeight;
        vTransStatusBar.setLayoutParams(params);
    }

    /**
     * 设置状态栏颜色
     */
    public void setStatusBarBackgroundColor(int color){
        vTransStatusBar.setBackgroundColor(color);
    }
}
