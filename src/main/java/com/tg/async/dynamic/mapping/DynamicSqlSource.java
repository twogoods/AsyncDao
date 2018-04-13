package com.tg.async.dynamic.mapping;

import com.tg.async.dynamic.xmltags.SqlNode;

import java.util.Map;

/**
 * Created by twogoods on 2018/4/13.
 */
public class DynamicSqlSource implements SqlSource {

    private final SqlNode rootSqlNode;

    public DynamicSqlSource(SqlNode rootSqlNode) {
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public void getBoundSql(Object parameterObject) {

    }

}
