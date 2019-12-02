package com.hykj.base.utils.text;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.base.listener.SingleOnClickListener;

/**
 * created by cjf
 * on: 2019/11/27
 * 用户协议和隐私政策工具类
 */
public class UserPrivateUtils {
    private ImageView ivIcon;//图标控件
    private TextView tvText;//文本控件

    private String text;//用户协议和隐私政策文本
    private String[] clickTextArray;//可点击的文本数组
    private @ColorInt
    int clickTextColor;//可点击的文本字体颜色
    private @ColorInt
    int clickTextHighlightColor;//可点击的文本高亮颜色
    private boolean isUnderLine;//是否设置可点击的文本下划线
    private boolean isUserPrivate;//初始是否同意用户协议和隐私政策
    private OnClickAbleListener onClickableListener;

    private UserPrivateUtils(Builder builder) {
        this.ivIcon = builder.ivIcon;
        this.tvText = builder.tvText;
        this.text = builder.text;
        this.clickTextArray = builder.clickTextArray;
        this.clickTextColor = builder.clickTextColor;
        this.clickTextHighlightColor = builder.clickTextHighlightColor;
        this.isUnderLine = builder.isUnderLine;
        this.isUserPrivate = builder.isUserPrivate;
        this.onClickableListener = builder.onClickableListener;
        init();
    }

    //初始化
    private UserPrivateUtils init() {
        if (text != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            if (clickTextArray != null && clickTextArray.length > 0) {
                for (int i = 0; i < clickTextArray.length; i++) {
                    final String clickText = clickTextArray[i];
                    int index = text.indexOf(clickText);
                    if (index != -1) {
                        final int clickIndex = i;
                        builder.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                if (onClickableListener != null)
                                    onClickableListener.onClick(clickIndex, clickText, widget);
                            }
                        }, index, index + clickText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        builder.setSpan(new UnderlineSpan() {
                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                ds.setUnderlineText(isUnderLine);
                                if (clickTextColor != -1)
                                    ds.setColor(clickTextColor);
                            }
                        }, index, index + clickText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            tvText.setText(builder);
            tvText.setMovementMethod(LinkMovementMethod.getInstance());
            if (clickTextHighlightColor != -1) {
                tvText.setHighlightColor(clickTextHighlightColor);
            }
            if (this.ivIcon != null) {
                this.ivIcon.setSelected(isUserPrivate);
                this.ivIcon.setOnClickListener(new SingleOnClickListener() {
                    @Override
                    public void onClickSub(View v) {
                        v.setSelected(!v.isSelected());
                    }
                });
            }
        }
        return this;
    }

    /**
     * @return 用户是否同意用户协议和隐私政策 true同意 false 不同意
     */
    public boolean isUserPrivate() {
        return this.ivIcon != null && this.ivIcon.isSelected();
    }


    public static class Builder {
        private ImageView ivIcon;
        private TextView tvText;

        private String text;
        private String[] clickTextArray;
        private @ColorInt
        int clickTextColor = -1;
        private @ColorInt
        int clickTextHighlightColor = -1;
        private boolean isUnderLine;
        private boolean isUserPrivate = true;
        private OnClickAbleListener onClickableListener;

        public Builder(ImageView ivIcon, @NonNull TextView tvText) {
            this.ivIcon = ivIcon;
            this.tvText = tvText;
        }

        public Builder(@NonNull TextView tvText) {
            this.tvText = tvText;
        }

        public Builder isUserPrivate(boolean isUserPrivate) {
            this.isUserPrivate = isUserPrivate;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder clickTextArray(String[] clickTextArray) {
            this.clickTextArray = clickTextArray;
            return this;
        }

        public Builder clickTextColor(int clickTextColor) {
            this.clickTextColor = clickTextColor;
            return this;
        }

        public Builder clickTextHighlightColor(int clickTextHighlightColor) {
            this.clickTextHighlightColor = clickTextHighlightColor;
            return this;
        }

        public Builder underLine(boolean underLine) {
            isUnderLine = underLine;
            return this;
        }

        public Builder onClickableListener(OnClickAbleListener onClickableListener) {
            this.onClickableListener = onClickableListener;
            return this;
        }

        public UserPrivateUtils build() {
            return new UserPrivateUtils(this);
        }
    }

    public interface OnClickAbleListener {
        /**
         * @param index     clickTextArray里面的当前点击位置
         * @param clickText 当前点击文本
         * @param widget    控件
         */
        void onClick(int index, String clickText, @NonNull View widget);
    }
}
