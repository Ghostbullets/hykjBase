package com.hykj.base.utils.view;

import android.support.annotation.IntRange;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Pattern;

public class EditTextUtils {

    /**
     * 设置小数点后保留几位小数
     *
     * @param editText  控件
     * @param keepPlace 小数点后保留几位，最少为1
     */
    public static void setInputFilterNumDecimal(EditText editText, @IntRange(from = 1, to = Integer.MAX_VALUE) final int keepPlace) {
        InputFilter filter = new InputFilter() {
            // source:当前输入的字符；start:输入字符的开始位置；end:输入字符的结束位置
            // dest：当前已显示的内容；dstart:当前光标开始位置；dent:当前光标结束位置
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" "))
                    return "";
                if (dest.length() == 0 && source.equals(".")) {//输入的第一个字符是小数点,直接返回0.1
                    return "0.";
                }
                if (dest.length() == 1 && dest.toString().equals("0") && source.equals("0")) {//连续输入2个0，直接返回空字符串
                    return "";
                }
                String[] split = dest.toString().split("\\.");
                if (split.length > 1) {
                    String decimal = split[1];
                    if (decimal.length() == keepPlace) {//输入框小数的位数
                        return "";
                    }
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 设置允许输入中文、字母、数字
     *
     * @param editText 控件
     */
    public static void setInputFilterCharOrNum(EditText editText) {
        InputFilter filter = new InputFilter() {
            // source:当前输入的字符；start:输入字符的开始位置；end:输入字符的结束位置
            // dest：当前已显示的内容；dstart:当前光标开始位置；dent:当前光标结束位置
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String regex = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$";
                if (!Pattern.matches(regex, source.toString())) {
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
}
