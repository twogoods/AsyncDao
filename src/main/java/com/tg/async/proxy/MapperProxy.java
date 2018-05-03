package com.tg.async.proxy;

import com.github.mauricio.async.db.QueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ResultMap;
import com.tg.async.mysql.Configuration;
import com.tg.async.mysql.SQLConnection;
import com.tg.async.utils.DataConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.extern.slf4j.Slf4j;

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
        final MapperMethod mapperMethod = getMapperMethod(method);

        MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());

        DataHandler handler = null;
        if (args[args.length - 1] instanceof DataHandler) {
            handler = (DataHandler) args[args.length - 1];
        }
        switch (mappedStatement.getSqlType()) {
            case INSERT: {

            }
            case UPDATE: {

            }
            case DELETE: {

            }
            case SELECT: {

            }
            case UNKNOWN: {

            }
        }


        return null;
    }


    private interface ExcuteSQLhandle {
        void handle(MapperMethod mapperMethod, QueryResult queryResult, ResultMap resultMap, DataHandler dataHandler);
    }


    private class SelectHandle implements ExcuteSQLhandle {
        @Override
        public void handle(MapperMethod mapperMethod, QueryResult queryResult, ResultMap resultMap, DataHandler dataHandler) {
            if (mapperMethod.isReturnsMany()) {
                List list = DataConverter.queryResultToListObject(queryResult, mapperMethod.getPrimary(), resultMap);
                dataHandler.handle(list);
            } else if (mapperMethod.isReturnsMap()) {
                dataHandler.handle(DataConverter.queryResultToMap(queryResult, resultMap));
            } else if (mapperMethod.isReturnsSingle()) {
                dataHandler.handle(DataConverter.queryResultToObject(queryResult, mapperMethod.getPrimary(), resultMap));
            } else if (mapperMethod.isReturnsVoid()) {
                dataHandler.handle(null);
            }
        }
    }


    private class InsertHandle implements ExcuteSQLhandle {
        @Override
        public void handle(MapperMethod mapperMethod, QueryResult queryResult, ResultMap resultMap, DataHandler dataHandler) {
            if (mapperMethod.isReturnsMany()) {
                List list = DataConverter.queryResultToListObject(queryResult, mapperMethod.getPrimary(), resultMap);
                dataHandler.handle(list);
            } else if (mapperMethod.isReturnsMap()) {
                dataHandler.handle(DataConverter.queryResultToMap(queryResult, resultMap));
            } else if (mapperMethod.isReturnsSingle()) {
                dataHandler.handle(DataConverter.queryResultToObject(queryResult, mapperMethod.getPrimary(), resultMap));
            } else if (mapperMethod.isReturnsVoid()) {
                dataHandler.handle(null);
            }
        }
    }

    private void execute(MapperMethod mapperMethod, MappedStatement mappedStatement, Object[] args, DataHandler dataHandler, ExcuteSQLhandle excuteSQLhandle) {
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(convertArgs(mapperMethod, args));
        log.debug("sql : {}", boundSql);
        configuration.getConnectionPool().getConnection(asyncConnection -> {
            SQLConnection connection = asyncConnection.result();
            connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), new Handler<AsyncResult<QueryResult>>() {
                @Override
                public void handle(AsyncResult<QueryResult> asyncResult) {
                    if (asyncResult.succeeded()) {
                        QueryResult queryResult = asyncResult.result();
                        ResultMap resultMap = configuration.getResultMap(mapperMethod.getIface().getName() + "." + mappedStatement.getResultMap());
                        excuteSQLhandle.handle(mapperMethod,queryResult,resultMap,dataHandler);
                    } else {
                        log.error("execute sql error {}", asyncResult.cause());
                    }
                }
            });
        });
    }

    private void select(MapperMethod mapperMethod, MappedStatement mappedStatement, Object[] args, DataHandler dataHandler) {
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(convertArgs(mapperMethod, args));
        log.debug("sql : {}", boundSql);

        configuration.getConnectionPool().getConnection(asyncConnection -> {
            SQLConnection connection = asyncConnection.result();

            connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), new Handler<AsyncResult<QueryResult>>() {
                @Override
                public void handle(AsyncResult<QueryResult> asyncResult) {
                    if (asyncResult.succeeded()) {
                        QueryResult queryResult = asyncResult.result();
                        ResultMap resultMap = configuration.getResultMap(mapperMethod.getIface().getName() + "." + mappedStatement.getResultMap());

                        //TODO select insert 自增id

                    } else {
                        log.error("execute sql error {}", asyncResult.cause());
                    }
                }
            });
        });
    }

    private void insert(MapperMethod mapperMethod, MappedStatement mappedStatement, Object[] args, DataHandler dataHandler) {
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(convertArgs(mapperMethod, args));
        log.debug("sql : {}", boundSql);

        configuration.getConnectionPool().getConnection(asyncConnection -> {
            SQLConnection connection = asyncConnection.result();
            connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), new Handler<AsyncResult<QueryResult>>() {
                @Override
                public void handle(AsyncResult<QueryResult> asyncResult) {
                    if (asyncResult.succeeded()) {
                        QueryResult queryResult = asyncResult.result();
                        ResultMap resultMap = configuration.getResultMap(mapperMethod.getIface().getName() + "." + mappedStatement.getResultMap());


                        //TODO select insert 自增id
                        DataHandler handler = (DataHandler) args[args.length - 1];
                        if (mapperMethod.isReturnsMany()) {
                            List list = DataConverter.queryResultToListObject(queryResult, mapperMethod.getPrimary(), resultMap);
                            handler.handle(list);
                        } else if (mapperMethod.isReturnsMap()) {
                            handler.handle(DataConverter.queryResultToMap(queryResult, resultMap));
                        } else if (mapperMethod.isReturnsSingle()) {
                            handler.handle(DataConverter.queryResultToObject(queryResult, mapperMethod.getPrimary(), resultMap));
                        } else if (mapperMethod.isReturnsVoid()) {
                            handler.handle(null);
                        }
                    } else {
                        log.error("execute sql error {}", asyncResult.cause());
                    }
                }
            });
        });
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
