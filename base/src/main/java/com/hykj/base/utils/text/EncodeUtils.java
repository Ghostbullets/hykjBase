package com.hykj.base.utils.text;

import android.text.TextUtils;

public class EncodeUtils {

    /**
     * 字符串转unicode
     *
     * @param str 要转换成unicode编码的字符串
     * @return
     */
    public static String strToUnicode(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    /**
     * unicode转字符串
     *
     * @param unicode 要转换成普通字符串的unicode编码字符串
     * @return
     */
    public static String unicodeToStr(String unicode) {
        StringBuilder string = new StringBuilder();
        if (TextUtils.isEmpty(unicode))
            return null;
        String[] hex = unicode.split("\\\\u");
        for (String str : hex) {
            int data = Integer.parseInt(str, 16);
            string.append((char) data);
        }
        return string.toString();
    }
}
