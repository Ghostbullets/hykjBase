package com.hykj.base.utils.text;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

public class StringUtils {

    /**
     * 获取第一个不为空的字符串
     *
     * @param arrString
     * @return
     */
    public static String getNotEmptyStr(String... arrString) {
        for (String strItem : arrString) {
            if (!TextUtils.isEmpty(strItem))
                return strItem;
        }
        return "";
    }

    /**
     * 在字符串数组中追加分隔符
     *
     * @param strJoin
     * @param checkEmpty 是否在空字符串后面也加上分隔符 true不加，false加
     * @param arrString
     * @return
     */
    public static String join(String strJoin, boolean checkEmpty, String... arrString) {

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (int i = 0; i < arrString.length; i++) {
            if (checkEmpty && TextUtils.isEmpty(arrString[i])) {
                continue;
            }
            if (isFirst)
                isFirst = false;
            else
                sb.append(strJoin);

            sb.append(arrString[i]);
        }
        return sb.toString();
    }

    /**
     * 传入要换行的字符串，返回换行后的字符串
     *
     * @param length  每一行个数，超过该个数换行
     * @param content 要换行的字符串
     * @return
     */
    public static CharSequence getNewLineStr(@IntRange(from = 1) int length, String content) {//哈哈哈哈\n啦啦啦啦\n哈哈哈
        if (TextUtils.isEmpty(content))
            return content;
        int row = 1;
        if (row * length > content.length())
            return content;
        StringBuilder sb = new StringBuilder();
        while (row * length <= content.length()) {
            sb.append(content.substring((row - 1) * length, row * length));
            sb.append("\n");
            row++;
        }
        if ((row - 1) * length < content.length()) {//如果content.length是lineNum的倍数，则false，否则true
            sb.append(content.substring((row - 1) * length, content.length()));
        } else {
            sb.delete(sb.lastIndexOf("\n"), sb.length());
        }
        return sb;
    }

    /**
     * 是否都为空
     *
     * @param arrString
     * @return
     */
    public static boolean isEmpty(String... arrString) {
        for (String strItem : arrString) {
            if (!TextUtils.isEmpty(strItem))
                return false;
        }
        return true;
    }

    /**
     * 是否都不为空判断
     *
     * @param arrString
     * @return
     */
    public static boolean isNotEmpty(String... arrString) {
        for (String strItem : arrString) {
            if (TextUtils.isEmpty(strItem))
                return false;
        }
        return true;
    }

    /**
     * 判断 text字符串是否跟数组strs中的某一个元素相同
     *
     * @param text 字符串
     * @param strs 字符串数组
     * @return
     */
    public static boolean isEqual(String text, String... strs) {
        if (text == null || strs == null || strs.length == 0)
            return false;
        for (String str : strs) {
            if (str != null && str.equals(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取默认值
     *
     * @param strValue
     * @return
     */
    public static String getValueByDefault(String strValue) {
        return getValueByDefault(strValue, "");
    }

    /**
     * 获取默认值
     *
     * @param strValue
     * @param strDefault
     * @return
     */
    public static String getValueByDefault(String strValue, @NonNull String strDefault) {
        if (isEmpty(strValue))
            return strDefault;
        else
            return strValue;
    }

    /**
     * 获取32位uuid
     *
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成唯一号
     *
     * @return
     */
    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
