package com.tg.async.proxy;

import com.github.mauricio.async.db.QueryResult;
import com.github.mauricio.async.db.mysql.MySQLQueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.mapping.SqlType;
import com.tg.async.mysql.Configuration;
import com.tg.async.mysql.SQLConnection;
import com.tg.async.utils.DataConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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


    private Map<SqlType, ExcuteSQLhandle> sqlHandle = new HashMap<>(4);

    public MapperProxy(Configuration configuration, Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        this.configuration = configuration;

        sqlHandle.put(SqlType.INSERT, new InsertHandle());
        sqlHandle.put(SqlType.UPDATE, new UpdateHandle());
        sqlHandle.put(SqlType.DELETE, new DeleteHandle());
        sqlHandle.put(SqlType.SELECT, new SelectHandle());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isDefaultMethod(method)) {
            return invokeDefaultMethod(proxy, method, args);
        }
        MapperMethod mapperMethod = getMapperMethod(method);
        MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());

        DataHandler handler = null;
        if (args[args.length - 1] instanceof DataHandler) {
            handler = (DataHandler) args[args.length - 1];
        }
        switch (mappedStatement.getSqlType()) {
            case INSERT: {
                execute(mapperMethod, mappedStatement, args, handler, sqlHandle.get(SqlType.INSERT));
                break;
            }
            case UPDATE: {
                execute(mapperMethod, mappedStatement, args, handler, sqlHandle.get(SqlType.UPDATE));
                break;
            }
            case DELETE: {
                execute(mapperMethod, mappedStatement, args, handler, sqlHandle.get(SqlType.DELETE));
                break;
            }
            case SELECT: {
                execute(mapperMethod, mappedStatement, args, handler, sqlHandle.get(SqlType.SELECT));
                break;
            }
        }
        return null;
    }


    private interface ExcuteSQLhandle {
        void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap, DataHandler dataHandler);
    }


    private abstract class BaseSQLhandle implements ExcuteSQLhandle {
        protected void handleReturnData(MapperMethod mapperMethod, DataHandler dataHandler, long count, boolean key) {
            if (Integer.TYPE.equals(mapperMethod.getPrimary()) || Integer.class.equals(mapperMethod.getPrimary())) {
                try {
                    dataHandler.handle(Integer.parseInt(Long.toString(count)));
                } catch (NumberFormatException e) {
                    errorHandle(key, count);
                }
            } else if (Long.TYPE.equals(mapperMethod.getPrimary()) || Long.class.equals(mapperMethod.getPrimary())) {
                dataHandler.handle(count);
            } else if (Boolean.TYPE.equals(mapperMethod.getPrimary()) || Boolean.class.equals(mapperMethod.getPrimary())) {
                if (count > 0) {
                    dataHandler.handle(true);
                } else {
                    dataHandler.handle(false);
                }
            } else {
                dataHandler.handle(null);
            }
        }

        protected abstract void errorHandle(boolean key, long count);
    }


    private class SelectHandle extends BaseSQLhandle {
        @Override
        public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap, DataHandler dataHandler) {
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

        @Override
        protected void errorHandle(boolean key, long count) {
        }
    }


    private class InsertHandle extends BaseSQLhandle {
        @Override
        public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap, DataHandler dataHandler) {
            MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());
            if ("true".equals(mappedStatement.getUseGeneratedKeys())) {
                long generatedKey = ((MySQLQueryResult) queryResult).lastInsertId();
                handleReturnData(mapperMethod, dataHandler, generatedKey, true);
            } else {
                long rowsAffected = queryResult.rowsAffected();
                handleReturnData(mapperMethod, dataHandler, rowsAffected, false);
            }
        }

        @Override
        protected void errorHandle(boolean key, long count) {
            if (key) {
                log.error("generatedKey is {}, can't convert int ,please change int to long in return type", count);
            } else {
                log.error("rowsAffected is {}, can't convert int ,please change int to long in return type", count);
            }
        }
    }


    private class UpdateHandle extends BaseSQLhandle {
        @Override
        public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap, DataHandler dataHandler) {
            MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());
            handleReturnData(mapperMethod, dataHandler, queryResult.rowsAffected(), false);
        }

        @Override
        protected void errorHandle(boolean key, long count) {
            log.error("update row count is {}, can't convert int ,please change int to long in return type", count);
        }
    }

    private class DeleteHandle extends BaseSQLhandle {
        @Override
        public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap, DataHandler dataHandler) {
            MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());
            handleReturnData(mapperMethod, dataHandler, queryResult.rowsAffected(), false);
        }

        @Override
        protected void errorHandle(boolean key, long count) {
            log.error("delete row count is {}, can't convert int ,please change int to long in return type", count);
        }
    }


    protected void getConnection(Handler<AsyncResult<SQLConnection>> handler) {
        configuration.getConnectionPool().getConnection(res -> handler.handle(Future.succeededFuture(res.result())));
    }


    private void execute(MapperMethod mapperMethod, MappedStatement mappedStatement, Object[] args, DataHandler dataHandler, ExcuteSQLhandle excuteSQLhandle) {
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(convertArgs(mapperMethod, args));
        log.debug("sql : {}", boundSql);
        getConnection(asyncConnection -> {
            SQLConnection connection = asyncConnection.result();
//            connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), new Handler<AsyncResult<QueryResult>>() {
//                @Override
//                public void handle(AsyncResult<QueryResult> asyncResult) {
//                    if (asyncResult.succeeded()) {
//                        QueryResult queryResult = asyncResult.result();
//                        ModelMap resultMap;
//                        if (StringUtils.isEmpty(mappedStatement.getResultMap())) {
//                            resultMap = configuration.getModelMap(mappedStatement.getResultType());
//                        } else {
//                            resultMap = configuration.getModelMap(mapperMethod.getIface().getName() + "." + mappedStatement.getResultMap());
//                        }
//                        excuteSQLhandle.handle(mapperMethod, queryResult, resultMap, dataHandler);
//                    } else {
//                        log.error("execute sql error {}", asyncResult.cause());
//                    }
//                }
//            });
            connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), qr->{
                    if (qr.succeeded()) {
                        QueryResult queryResult = qr.result();
                        ModelMap resultMap;
                        if (StringUtils.isEmpty(mappedStatement.getResultMap())) {
                            resultMap = configuration.getModelMap(mappedStatement.getResultType());
                        } else {
                            resultMap = configuration.getModelMap(mapperMethod.getIface().getName() + "." + mappedStatement.getResultMap());
                        }
                        excuteSQLhandle.handle(mapperMethod, queryResult, resultMap, dataHandler);
                    } else {
                        log.error("execute sql error {}", qr.cause());
                    }
                }
            );
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
