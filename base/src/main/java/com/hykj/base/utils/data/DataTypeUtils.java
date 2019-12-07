package com.hykj.base.utils.data;

import androidx.annotation.NonNull;

/**
 * created by cjf
 * on: 2019/10/10
 * 基本数据类型工具类
 */
public class DataTypeUtils {

    public static Long getValueByDefault(Long value) {
        return getValueByDefault(value, 0L);
    }

    public static Long getValueByDefault(Long value, @NonNull Long longDefault) {
        return value == null ? longDefault : value;
    }

    public static Integer getValueByDefault(Integer value) {
        return getValueByDefault(value, 0);
    }

    public static Integer getValueByDefault(Integer value, @NonNull Integer intDefault) {
        return value == null ? intDefault : value;
    }

    public static Double getValueByDefault(Double value) {
        return getValueByDefault(value, 0.0);
    }

    public static Double getValueByDefault(Double value, @NonNull Double doubleDefault) {
        return value == null ? doubleDefault : value;
    }

    public static Boolean getValueByDefault(Boolean value) {
        return getValueByDefault(value, false);
    }

    public static Boolean getValueByDefault(Boolean value, @NonNull Boolean doubleDefault) {
        return value == null ? doubleDefault : value;
    }

    public static Float getValueByDefault(Float value) {
        return getValueByDefault(value, 0f);
    }

    public static Float getValueByDefault(Float value, @NonNull Float doubleDefault) {
        return value == null ? doubleDefault : value;
    }
}
