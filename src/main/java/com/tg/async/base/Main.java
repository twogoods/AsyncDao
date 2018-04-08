package com.tg.async.base;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by twogoods on 2018/3/23.
 */
public class Main {
    public static void main(String[] args) {
        Method[] methods = CommonDao.class.getMethods();

        for (Method method : methods) {
            System.out.println(Arrays.toString(method.getGenericParameterTypes()));
            System.out.println(Arrays.toString(method.getParameterTypes()));


            System.out.println(method.getGenericParameterTypes()[1] instanceof ParameterizedType);
            Type t = method.getGenericParameterTypes()[1];
            Type[] handleTypes = ((ParameterizedType) t).getActualTypeArguments();
            System.out.println(handleTypes[0]);
            System.out.println(handleTypes[0] instanceof ParameterizedType);


            Type[] collType = ((ParameterizedType) handleTypes[0]).getActualTypeArguments();
            System.out.println(collType[0]);
            System.out.println(collType[0] instanceof Class);


        }


    }
}
