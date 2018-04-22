package com.tg.async.mysql;

import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ResultMap;
import com.tg.async.mysql.pool.ConnectionPool;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 2018/4/22.
 */
public class Configuration {

    private ConnectionPool connectionPool;

    private Map<String, MappedStatement> statementCache = new HashMap<>();

    private Map<String, ResultMap> resultMapCache = new HashMap<>();

    private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

    public void setConnectionPool(com.tg.async.mysql.pool.ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void addMappedStatement(String key, MappedStatement mappedStatement) {
        statementCache.put(key, mappedStatement);
    }

    public MappedStatement getMappedStatement(String key) {
        return statementCache.get(key);
    }


    public void addResultMap(String key, ResultMap resultMap) {
        resultMapCache.put(key, resultMap);
    }

    public ResultMap getResultMap(String key) {
        return resultMapCache.get(key);
    }


    public MapperMethod getMapperMethod(Method method) {
        return methodCache.get(method);
    }

    public void addMapperMethod(Method method, MapperMethod mapperMethod) {
        methodCache.put(method, mapperMethod);
    }

}
