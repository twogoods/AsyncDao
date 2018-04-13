package com.tg.async.proxy;

import java.lang.reflect.Proxy;

/**
 * Created by twogoods on 2018/4/12.
 */
public class MapperProxyFactory<T> {

    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

    public T newInstance() {
        final MapperProxy<T> mapperProxy = new MapperProxy<T>(mapperInterface);
        return newInstance(mapperProxy);
    }
}
