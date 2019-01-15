package com.hykj.base.utils;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebViewUtils {

    /**
     * 将类以及它的子类定义的属性转成json字符串
     *
     * @param cls     类
     * @param isSuper 是否将它的子类定义的属性也取出来，(注：不包含Object类)
     * @return
     */
    public static Map<String, String> getAllField(Class<?> cls, boolean isSuper) {
        Map<String, String> map = new LinkedHashMap<>();
        if (cls != null) {
            do {
                try {
                    Field[] fields = cls.getDeclaredFields();
                    for (Field field : fields) {
                        String key = field.getName();
                        if ("serialVersionUID".equals(key))
                            continue;
                        String value;
                        field.setAccessible(true);
                        Object obj = field.get(cls);
                        if (obj == null)
                            continue;
                        if (obj instanceof String) {
                            value = (String) obj;
                        } else {
                            value = new Gson().toJson(obj);
                        }
                        map.put(key, value);
                    }
                    cls = isSuper ? cls.getSuperclass() : null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } while (cls != null && !cls.getName().equals(Object.class.getName()));
        }
        return map;
    }

    /**
     * WebView使用postUrl时需要，传递的数据必须先使用"application/x-www-form-urlencoded"编码
     *
     * @param params
     * @return
     */
    public static String concatParams(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0)
            return null;
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            builder.append(String.format("%s=%s&", next.getKey(), URLEncoder.encode(next.getValue(), "UTF-8")));
        }
        builder.deleteCharAt(builder.lastIndexOf("&"));
        return builder.toString();
    }
}
