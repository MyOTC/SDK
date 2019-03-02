package com.bdd.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * 反射工具类
 */
class ReflectionUtils {

    static Field findField(Class<?> clazz, String name) {
        Field[] fields = clazz.getDeclaredFields();
        int len = fields.length;

        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }


    static Method findMethod(Class<?> clazz, String name) {
        Method[] methods = clazz.isInterface() ? clazz.getMethods() : clazz.getDeclaredMethods();
        int len = methods.length;

        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 根据传入的方法对象，调用对应的方法
     *
     * @param method
     *            方法对象
     * @param obj
     *            要调用的实例对象【如果是调用静态方法，则可以传入null】
     * @param args
     *            传入方法的实参【可以不写】
     * @return 方法的返回值【没有返回值，则返回null】
     */
    static Object invokeMethod(Method method, Object obj, Object... args)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
