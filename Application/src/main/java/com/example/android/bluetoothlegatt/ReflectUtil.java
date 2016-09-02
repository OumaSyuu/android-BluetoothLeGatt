package com.example.android.bluetoothlegatt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/8/29.
 */

public class ReflectUtil {

    private ReflectUtil() {}

    /**
     *  invoke with single param
     * @param target
     * @param methodName
     * @param param
     * @param <T>
     * @param <P>
     * @return
     */
    public static <T, P> Object invoke(T target, String methodName, P param) {

        try {
            Method method = target.getClass().getMethod(methodName, param.getClass());
            return method.invoke(target, param);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * invoke without params
     * @param target
     * @param methodName
     * @param <T>
     * @return
     */
    public static <T> Object invoke(T target, String methodName) {

        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

}
