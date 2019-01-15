package com.hykj.base.utils;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class ReflectionUtils {

    /**
     *  调用Class的无参方法
     */
    public static Object invokeMethod(Object obj,String methodName, Object[] params){
        return invokeMethod(obj,methodName,null,null);
    }

    /**
     * 通过类对象，运行指定方法
     *
     * @param obj        类对象
     * @param methodName 方法名
     * @param paramTypes 参数对应的类型（如果不指定，那么从params来判断，可能会判断不准确，例如把CharSequence 判断成String，导致反射时方法找不到）
     * @param params     参数值
     * @return 失败返回null
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] params){
        if (obj==null||TextUtils.isEmpty(methodName))
            return null;
        Class<?> clazz = obj.getClass();
        try {
            if (paramTypes==null){
                if (params!=null){
                    paramTypes=new Class[params.length];
                    for (int i=0;i<params.length;i++){
                        paramTypes[i]=params[i].getClass();
                    }
                }
            }
            Method method = clazz.getMethod(methodName, paramTypes);//参数1是方法名，参数2是参数对应的类型
            method.setAccessible(true);
            return method.invoke(obj,params);//参数1是调用方法的对象，参数2是用于方法调用的参数
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param obj 类对象
     * @param fieldName 类属性名
     * @return 反射获取对象属性值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || TextUtils.isEmpty(fieldName)) return null;

        Class<?> clazz = obj.getClass();
        //当obj不是Object类时，获取对象属性值，属性值可能在obj的父类中，所以使用clazz.getSuperclass()返回直接继承的父类
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);//可能报NoSuchFieldException异常
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 反射修改对象属性值
     * @param obj 类对象
     * @param fieldName 类属性名
     * @param value 类属性名对应的属性值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null || TextUtils.isEmpty(fieldName)) return;

        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(fieldName, value);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 调用类的静态方法
     */
    public static <T> T invokeClassMethod(String classPath, String methodName, Class[] paramClasses, Object[] params){
        return (T) executeClassLoad(getClass(classPath),methodName,paramClasses,params);
    }

    /**
     * @param cls 类对象
     * @param methodName 静态方法名称
     * @param parameterTypes 参数类型
     * @param params 方法所需的参数
     * @return 调用类的静态方法
     */
    private static Object executeClassLoad(Class cls, String methodName, Class[] parameterTypes, Object[] params){
        Object obj = null;
        if (!(cls == null || checkObjExists(methodName))) {
            Method method = getMethod(cls, methodName, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
                try {
                    obj = method.invoke(null, params);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 根据类的包全名得到类对象
     * @param classPath 类的包全名
     * @return
     */
    public static Class getClass(String classPath) {
        Class cls = null;
        try {
            cls = Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cls;
    }

    /**
     * @param cls 类对象
     * @param methodName 静态方法名称
     * @param parameterTypes 参数类型
     * @return 得到类的静态方法
     */
    private static Method getMethod(Class cls, String methodName, Class[] parameterTypes) {
        if (cls == null || checkObjExists(methodName)) return null;

        try {
            cls.getMethods();
            cls.getDeclaredMethods();
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception e) {
            try {
                return cls.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e1) {
                return cls.getSuperclass() != null ? getMethod(cls.getSuperclass(), methodName, parameterTypes) : null;
            }
        }
    }

    private static boolean checkObjExists(Object obj) {
        return obj == null || obj.toString().equals("") || obj.toString().trim().equals("null");
    }
}
