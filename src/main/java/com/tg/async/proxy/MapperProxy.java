package com.tg.async.proxy;

import com.github.mauricio.async.db.ResultSet;
import com.github.mauricio.async.db.RowData;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.mysql.Configuration;
import com.tg.async.mysql.SQLConnection;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.extern.slf4j.Slf4j;
import scala.runtime.AbstractFunction1;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/12.
 */
@Slf4j
public class MapperProxy<T> implements InvocationHandler {

    private Configuration configuration;
    private Class<T> mapperInterface;

    public MapperProxy(Configuration configuration, Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        this.configuration = configuration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isDefaultMethod(method)) {
            return invokeDefaultMethod(proxy, method, args);
        }
        MapperMethod mapperMethod = getMapperMethod(method);
        MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(convertArgs(mapperMethod, args));
        log.debug("sql : {}", boundSql);


        configuration.getConnectionPool().getConnection(asyncConnection -> {
            SQLConnection connection = asyncConnection.result();
            DataHandler handler = null;
            if (args[args.length - 1] instanceof DataHandler) {
                handler = (DataHandler) args[args.length - 1];
            }
            connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), new Handler<AsyncResult<ResultSet>>() {
                @Override
                public void handle(AsyncResult<ResultSet> asyncResult) {
                    if (asyncResult.succeeded()) {

                        ResultSet resultSet = asyncResult.result();


                        resultSet.foreach(new AbstractFunction1<RowData, Void>() {
                            @Override
                            public Void apply(RowData row) {

                                row.foreach(new AbstractFunction1<Object, Void>() {
                                    @Override
                                    public Void apply(Object value) {
                                        System.out.println(value);
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                    } else {
                        asyncResult.cause().printStackTrace();
                    }

                }
            });
        });
        return null;
    }


    private Object convertArgs(MapperMethod mapperMethod, Object[] args) {
        Map<String, Object> param = new HashMap<>();
        List<String> params = mapperMethod.getParamName();
        for (int i = 0; i < params.size() - 1; i++) {
            param.put(params.get(i), args[i]);
        }
        return param;
    }


    private MapperMethod getMapperMethod(Method method) {
        MapperMethod mapperMethod = null;
        if ((mapperMethod = configuration.getMapperMethod(method)) == null) {
            mapperMethod = new MapperMethod(mapperInterface, method);
            configuration.addMapperMethod(method, mapperMethod);
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
