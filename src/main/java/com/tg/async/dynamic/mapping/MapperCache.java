package com.tg.async.dynamic.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/19.
 */
public class MapperCache {

    private static final Map<String, MappedStatement> statementCache = new HashMap<>();

    private static final Map<String, ResultMap> resultMapCache = new HashMap<>();


    public static void addMappedStatement(String key, MappedStatement mappedStatement) {
        statementCache.put(key, mappedStatement);
    }

    public static MappedStatement getMappedStatement(String key) {
        return statementCache.get(key);
    }


    public static void addResultMap(String key, ResultMap resultMap) {
        resultMapCache.put(key, resultMap);
    }

    public static ResultMap getResultMap(String key) {
        return resultMapCache.get(key);
    }


}
