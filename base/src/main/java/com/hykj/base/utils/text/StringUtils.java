package com.hykj.base.utils.text;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;

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
     * @param arrString
     * @return
     */
    /**
     *
     * @param strJoin
     * @param checkEmpty 是非为
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
     * 删除线
     *
     * @param str
     * @return
     */
    public static SpannableString getStrikethrough(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
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
