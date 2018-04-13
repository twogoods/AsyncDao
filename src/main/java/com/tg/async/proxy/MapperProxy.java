package com.tg.async.proxy;

import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 2018/4/12.
 */
public class MapperProxy<T> implements InvocationHandler {

    private Class<T> mapperInterface;
    private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

    public MapperProxy(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isDefaultMethod(method)) {
            return invokeDefaultMethod(proxy, method, args);
        }

        System.out.println(getMapperMethod(method));
        System.out.println(Arrays.toString(args));

        DataHandler handler;
        if (args[args.length - 1] instanceof DataHandler) {
            handler = (DataHandler) args[args.length - 1];
            handler.handle(null);
        }
        return null;
    }


    private MapperMethod getMapperMethod(Method method) {
        MapperMethod mapperMethod = null;
        if ((mapperMethod = methodCache.get(method)) == null) {
            mapperMethod = new MapperMethod(mapperInterface, method);
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }


    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
                && method.getDeclaringClass().isInterface();
    }
}
