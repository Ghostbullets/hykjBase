package com.hykj.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.text.StringUtils;

/**
 * 空布局View
 */
public class CustomEmptyView extends LinearLayout {
    private static final int TEXT_SIZE = 12;
    private ImageView emptyIcon;
    private TextView emptyDescribe;
    private TextView tvRefresh;
    private String netWorkErrorText = "网络错误,请试试看刷新页面";
    private String emptyText;
    private onRefreshClickListener onRefreshClickListener;


    public CustomEmptyView(Context context) {
        this(context, null);
    }

    public CustomEmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    @SuppressLint("RestrictedApi")
    public CustomEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.weight_custom_empty_view, this, true);
        emptyIcon = (ImageView) findViewById(R.id.iv_empty_icon);
        emptyDescribe = (TextView) findViewById(R.id.tv_empty_describe);
        tvRefresh = (TextView) findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(onClickListener);

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.CustomEmptyView, defStyleAttr, 0);

        int emptyIconId = a.getResourceId(R.styleable.CustomEmptyView_emptyIcon, -1);
        emptyText = a.getString(R.styleable.CustomEmptyView_emptyText);
        netWorkErrorText = a.getString(R.styleable.CustomEmptyView_netWorkErrorText);
        setEmptyViewInfo(emptyIconId, emptyText);

        int textColor = a.getColor(R.styleable.CustomEmptyView_emptyTextColor, getResources().getColor(R.color.gray_a8));
        setTextColor(textColor);

        int textSize = a.getDimensionPixelSize(R.styleable.CustomEmptyView_emptyTextSize, sp2px(context, TEXT_SIZE));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        a.recycle();
    }

    SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View view) {
            if (onRefreshClickListener != null) onRefreshClickListener.onRefreshClick(view);
        }
    };

    public void setEmptyViewInfo(Integer emptyIconId, CharSequence emptyText) {
        if (emptyIconId != null && emptyIconId != -1) {
            emptyIcon.setImageResource(emptyIconId);
            emptyIcon.setVisibility(VISIBLE);
        } else {
            emptyIcon.setVisibility(GONE);
        }
        if (emptyText != null) {
            this.emptyText = (String) emptyText;
            emptyDescribe.setText(emptyText);
        }
    }

    /**
     * 设置网络错误、没数据空布局
     *
     * @param isNetWorkError 是否是网络错误引起的空布局
     */
    public void updateEmptyViewInfo(boolean isNetWorkError) {
        emptyDescribe.setText(isNetWorkError ? StringUtils.getValueByDefault(netWorkErrorText) : StringUtils.getValueByDefault(emptyText));
        tvRefresh.setVisibility(isNetWorkError ? View.VISIBLE : View.GONE);
    }


    public void setTextColor(Integer colorId) {
        if (colorId != null) emptyDescribe.setTextColor(colorId);
    }

    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setTextSize(int unit, float size) {
        emptyDescribe.setTextSize(unit, size);
    }

    public ImageView getEmptyIcon() {
        return emptyIcon;
    }

    public TextView getEmptyDescribe() {
        return emptyDescribe;
    }

    public TextView getTvRefresh() {
        return tvRefresh;
    }

    /**
     * sp转换成px
     */
    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setOnRefreshClickListener(CustomEmptyView.onRefreshClickListener onRefreshClickListener) {
        this.onRefreshClickListener = onRefreshClickListener;
    }

    public interface onRefreshClickListener {
        void onRefreshClick(View v);
    }
}

