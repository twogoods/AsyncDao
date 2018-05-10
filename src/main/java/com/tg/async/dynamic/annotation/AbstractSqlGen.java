package com.tg.async.dynamic.annotation;

import com.tg.async.dynamic.annotation.where.AbstractWhereSqlGen;
import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.dynamic.mapping.DynamicSqlSource;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.MixedSqlNode;
import com.tg.async.dynamic.xmltags.SqlNode;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by twogoods on 2018/5/6.
 */
public abstract class AbstractSqlGen implements SqlGen {
    protected Method method;
    protected ModelMap modelMap;
    protected AbstractWhereSqlGen abstractWhereSqlGen;

    public AbstractSqlGen(Method method, ModelMap modelMap) {
        this.method = method;
        this.modelMap = modelMap;
    }

    protected abstract SqlNode generateBaseSql();

    protected SqlNode generateWhereSql() {
        return abstractWhereSqlGen.generateWhereSql();
    }

    protected SqlNode generateSuffixSql() {
        return null;
    }

    @Override
    public MappedStatement generate() {
        MixedSqlNode rootSqlNode = new MixedSqlNode(Arrays.asList(generateBaseSql(), generateWhereSql()));
        MappedStatement mappedStatement = new MappedStatement.Builder(buildKey(), new DynamicSqlSource(rootSqlNode), "select")
                .keyGenerator(null)
                .keyProperty(null)
                .parameterType(null)
                .resultType(null)
                .resultMap(null)
                .build();
        return mappedStatement;
    }


    protected String getColumnByField(String field) {
        ColumnMapping columnMapping = modelMap.getFieldKeyMappings().get(field);
        return columnMapping == null ? field : columnMapping.getColumn();
    }

    private String buildKey() {
        return method.getClass().getName() + "." + method.getName();
    }
}
