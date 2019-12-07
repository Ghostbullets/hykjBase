package com.hykj.base.utils.text;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;

import com.hykj.base.bean.DistanceInfo;
import com.hykj.base.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Spannable工具类
 */
public class SpanUtils {

    public static CharSequence getPriceSizeSpan(@NonNull CharSequence sequence, @FloatRange(from = 0) float proportion) {
        return getRelativeSizeSpan(sequence, proportion, 0, 1);
    }

    /**
     * @param sequence   文本
     * @param proportion 相对文本字体的缩放比例
     * @param start      开始缩放位置
     * @param end        结束缩放位置
     * @return 字符串
     */
    public static CharSequence getRelativeSizeSpan(@NonNull CharSequence sequence, @FloatRange(from = 0) float proportion, int start, int end) {
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
    public static CharSequence getRelativeSizeSpan(int index, @FloatRange(from = 0) float proportion, @NonNull CharSequence... sequences) {
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
            if (index == i) {
                end = builder.length();
                break;
            }
        }
        if (start >= 0 && start < end) {
            builder.setSpan(new RelativeSizeSpan(proportion), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * @param relativeSizeStr 要缩放的字符串片段
     * @param proportion   相对文本字体的缩放比例
     * @param contrastStr  要拿来对照的字符串
     * @return
     */
    public static CharSequence getRelativeSizeSpan(String relativeSizeStr, @FloatRange(from = 0) float proportion, @NonNull String contrastStr) {
        if (TextUtils.isEmpty(relativeSizeStr))
            return contrastStr;
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = relativeSizeStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(relativeSizeStr, end);
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
     * @param sequence 文本
     * @param size     设置文字大小为多少sp
     * @param start    开始缩放位置
     * @param end      结束缩放位置
     * @return
     */
    public static CharSequence getAbsoluteSizeSpan(@NonNull CharSequence sequence, @IntRange(from = 10) int size, int start, int end) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        builder.setSpan(new AbsoluteSizeSpan(DisplayUtils.size2px(size, TypedValue.COMPLEX_UNIT_SP)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置字体大小
     *
     * @param index     设置哪一段字符串的文字大小
     * @param size      设置文字大小为多少sp
     * @param sequences 字符串集合
     * @return
     */
    public static CharSequence getAbsoluteSizeSpan(int index, @IntRange(from = 10) int size, @NonNull CharSequence... sequences) {
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
            if (index == i) {
                end = builder.length();
                break;
            }
        }
        if (start >= 0 && start < end) {
            builder.setSpan(new AbsoluteSizeSpan(DisplayUtils.size2px(size, TypedValue.COMPLEX_UNIT_SP)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * @param absoluteSizeStr 要设置字体大小的字符串片段
     * @param size            设置文字大小为多少sp
     * @param contrastStr     要拿来对照的字符串
     * @return
     */
    public static CharSequence getAbsoluteSizeSpan(String absoluteSizeStr, @IntRange(from = 10) int size, @NonNull String contrastStr) {
        if (TextUtils.isEmpty(absoluteSizeStr))
            return contrastStr;
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = absoluteSizeStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(absoluteSizeStr, end);
            if (index == -1) {
                break;
            }
            end += index + length;
            distanceInfos.add(new DistanceInfo(index, end, length));
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(contrastStr);
        for (DistanceInfo info : distanceInfos) {
            builder.setSpan(new AbsoluteSizeSpan(DisplayUtils.size2px(TypedValue.COMPLEX_UNIT_SP, size)), info.start, info.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
    public static CharSequence getColorSizeSpan(int start, int end, @ColorInt int color, @NonNull CharSequence sequence) {
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
    public static CharSequence getColorSizeSpan(int index, @ColorInt int color, @NonNull CharSequence... sequences) {
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
            if (index == i) {
                end = builder.length();
                break;
            }
        }
        if (start >= 0 && start < end) {
            builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 设置颜色
     *
     * @param foregroundColorStr 要变色的字符串片段
     * @param color              颜色
     * @param contrastStr        要拿来对照的字符串
     * @return
     */
    public static CharSequence getColorSizeSpan(String foregroundColorStr, @ColorInt int color, @NonNull String contrastStr) {
        if (TextUtils.isEmpty(foregroundColorStr))
            return contrastStr;
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = foregroundColorStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(foregroundColorStr, end);
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

    /**
     * 设置删除线
     *
     * @param start    开始位置
     * @param end      结束位置
     * @param sequence 字符串
     * @return
     */
    public static CharSequence getStrikethroughSpan(int start, int end, @NonNull CharSequence sequence) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        builder.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置删除线
     *
     * @param index     设置哪一段字符串的删除线
     * @param sequences 字符串集合
     * @return
     */
    public static CharSequence getStrikethroughSpan(int index, @NonNull CharSequence... sequences) {
        if (index < 0 || index > sequences.length - 1)
            throw new RuntimeException("index must greater than or equal 0,less than sequences.length-1");
        int start = 0;
        int end = 0;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < sequences.length; i++) {
            if (index == i) {
                start = builder.length();
            }
            builder.append(sequences[i]);
            if (index == i) {
                end = builder.length();
                break;
            }
        }
        builder.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置删除线
     *
     * @param strikethroughStr 要设置删除线的字符串片段
     * @param contrastStr      要拿来对照的字符串
     * @return
     */
    public static CharSequence getStrikethroughSpan(String strikethroughStr, @NonNull String contrastStr) {
        if (TextUtils.isEmpty(strikethroughStr))
            return contrastStr;
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = strikethroughStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(strikethroughStr, end);
            if (index == -1)
                break;
            end += index + length;
            distanceInfos.add(new DistanceInfo(index, end, length));
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(contrastStr);
        for (DistanceInfo info : distanceInfos) {
            builder.setSpan(new StrikethroughSpan(), info.start, info.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 设置下划线
     *
     * @param start    开始位置
     * @param end      结束位置
     * @param sequence 字符串
     * @return
     */
    public static CharSequence getUnderlineSpan(int start, int end, @NonNull CharSequence sequence) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        builder.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置下划线
     *
     * @param index     设置哪一段字符串的删除线
     * @param sequences 字符串集合
     * @return
     */
    public static CharSequence getUnderlineSpan(int index, @NonNull CharSequence... sequences) {
        if (index < 0 || index > sequences.length - 1)
            throw new RuntimeException("index must greater than or equal 0,less than sequences.length-1");
        int start = 0;
        int end = 0;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < sequences.length; i++) {
            if (index == i) {
                start = builder.length();
            }
            builder.append(sequences[i]);
            if (index == i) {
                end = builder.length();
                break;
            }
        }
        builder.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 设置下划线
     *
     * @param underlineStr 要设置下划线的字符串片段
     * @param contrastStr  要拿来对照的字符串
     * @return
     */
    public static CharSequence getUnderlineSpan(String underlineStr, @NonNull String contrastStr) {
        if (TextUtils.isEmpty(underlineStr))
            return contrastStr;
        List<DistanceInfo> distanceInfos = new ArrayList<>();
        int length = underlineStr.length();
        int end = 0;
        while (end < contrastStr.length()) {
            int index = contrastStr.indexOf(underlineStr, end);
            if (index == -1)
                break;
            end += index + length;
            distanceInfos.add(new DistanceInfo(index, end, length));
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(contrastStr);
        for (DistanceInfo info : distanceInfos) {
            builder.setSpan(new UnderlineSpan(), info.start, info.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}
