package com.hykj.base.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.hykj.base.utils.ContextKeep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_data";
    public static final String TOKEN = "token";
    public static final String PHONE = "phone";

    private static SPUtils spUtils;
    private static SharedPreferences sp;

    private SPUtils() {
        sp = ContextKeep.getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtils init() {
        if (spUtils == null) {
            synchronized (SPUtils.class) {
                if (spUtils == null) {
                    spUtils = new SPUtils();
                }
            }
        }
        return spUtils;
    }

    /**
     * 设置参数
     *
     * @param key key
     * @param obj 属性值
     */
    public static void put(String key, Object obj) {
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof String) {
            editor.putString(key, (String) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, (Integer) obj);
        } else if (obj instanceof Long) {
            editor.putLong(key, (Long) obj);
        } else if (obj instanceof Float) {
            editor.putFloat(key, (Float) obj);
        } else if (obj instanceof Boolean) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Set) {
            editor.putStringSet(key, (Set<String>) obj);
        } else {
            editor.putString(key, obj.toString());
        }
        SharedPreferencesCommap.apply(editor);
    }

    /**
     * @param key           key
     * @param defaultValues 默认值
     * @return 得到属性值
     */
    public static Object get(String key, Object defaultValues) {
        if (defaultValues != null) {
            if (defaultValues instanceof String) {
                return sp.getString(key, (String) defaultValues);
            } else if (defaultValues instanceof Integer) {
                return sp.getInt(key, (Integer) defaultValues);
            } else if (defaultValues instanceof Long) {
                return sp.getLong(key, (Long) defaultValues);
            } else if (defaultValues instanceof Float) {
                return sp.getFloat(key, (Float) defaultValues);
            } else if (defaultValues instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) defaultValues);
            } else if (defaultValues instanceof Set) {
                return sp.getStringSet(key, (Set<String>) defaultValues);
            } else {
                return sp.getString(key, defaultValues.toString());
            }
        }
        return null;
    }

    /**
     * 删除key所对应的值
     *
     * @param key
     */
    public static void remove(String key) {
        SharedPreferencesCommap.apply(sp.edit().remove(key));
    }

    /**
     * 清空存储的所有值
     */
    public static void clear() {
        SharedPreferencesCommap.apply(sp.edit().clear());
    }

    /**
     * @param key key
     * @return 判断该key是否有存储在sp中
     */
    public static boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * @return 返回所有的键值对
     */
    public static Map<String, ?> getAll() {
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCommap {
        private static final Method method = findMethod();

        private static Method findMethod() {
            Method apply = null;
            try {
                apply = SharedPreferences.Editor.class.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return apply;
        }

        private static void apply(SharedPreferences.Editor editor) {
            try {
                if (method != null) {
                    method.invoke(editor);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }
}
