package com.hykj.base.utils.text;

import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.hykj.base.bean.DistanceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Spannable工具类
 */
public class SpanUtils {

    public static CharSequence getPriceSizeSpan(CharSequence sequence, @FloatRange(from = 0) float proportion) {
        return getRelativeSizeSpan(sequence, proportion, 0, 1);
    }

    /**
     * @param sequence   文本
     * @param proportion 相对文本字体的缩放比例
     * @param start      开始缩放位置
     * @param end        结束缩放位置
     * @return 字符串
     */
    public static CharSequence getRelativeSizeSpan(CharSequence sequence, @FloatRange(from = 0) float proportion, int start, int end) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        builder.setSpan(new RelativeSizeSpan(proportion), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置字体大小
     *
     * @param index      设置哪一段字符串的缩放比例
     * @param proportion 相对文本字体的缩放比例
     * @param sequences  字符串集合
     * @return
     */
    public static CharSequence getRelativeSizeSpan(int index, @FloatRange(from = 0) float proportion, CharSequence... sequences) {
        if (index < 0 || index > sequences.length - 1)
            throw new RuntimeException("index must greater than or equal 0,less than sequences.length-1");
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int start = 0;
        int end = 0;
        for (int i = 0; i < sequences.length; i++) {
            if (index == i) {
                start = builder.length();
            }
            builder.append(sequences[i]);
            end = builder.length();
        }
        if (start >= 0 && start < end) {
            builder.setSpan(new RelativeSizeSpan(proportion), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * @param sizeStr    要缩放的字符串片段
     * @param proportion       相对文本字体的缩放比例
     * @param contrastStr 要拿来对照的字符串
     * @return
     */
    public static CharSequence getRelativeSizeSpan(String sizeStr, @FloatRange(from = 0) float proportion, String contrastStr) {
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = sizeStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(sizeStr, end);
            if (index == -1) {
                break;
            }
            end += index + length;
            distanceInfos.add(new DistanceInfo(index, end, length));
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(contrastStr);
        for (DistanceInfo info : distanceInfos) {
            builder.setSpan(new RelativeSizeSpan(proportion), info.start, info.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 设置颜色
     *
     * @param start    开始位置
     * @param end      结束位置
     * @param color    颜色值
     * @param sequence 字符串
     * @return
     */
    public static CharSequence getColorSizeSpan(int start, int end, @ColorInt int color, CharSequence sequence) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置颜色
     *
     * @param index     设置哪一段字符串的颜色
     * @param color     颜色
     * @param sequences 字符串集合
     * @return
     */
    public static CharSequence getColorSizeSpan(int index, @ColorInt int color, CharSequence... sequences) {
        if (index < 0 || index > sequences.length - 1)
            throw new RuntimeException("index must greater than or equal 0,less than sequences.length-1");
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int start = 0;
        int end = 0;
        for (int i = 0; i < sequences.length; i++) {
            if (index == i) {
                start = builder.length();
            }
            builder.append(sequences[i]);
            end = builder.length();
        }
        if (start >= 0 && start < end) {
            builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /** 设置颜色
     * @param colorStr    要变色的字符串片段
     * @param color       颜色
     * @param contrastStr 要拿来对照的字符串
     * @return
     */
    public static CharSequence getColorSizeSpan(String colorStr, @ColorInt int color, String contrastStr) {
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = colorStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(colorStr, end);
            if (index == -1) {
                break;
            }
            end += index + length;
            distanceInfos.add(new DistanceInfo(index, end, length));
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(contrastStr);
        for (DistanceInfo info : distanceInfos) {
            builder.setSpan(new ForegroundColorSpan(color), info.start, info.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}
