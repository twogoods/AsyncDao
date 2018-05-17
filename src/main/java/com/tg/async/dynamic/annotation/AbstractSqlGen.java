package com.tg.async.dynamic.annotation;

import com.tg.async.dynamic.mapping.DynamicSqlSource;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.MixedSqlNode;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by twogoods on 2018/5/6.
 */
public abstract class AbstractSqlGen implements SqlGen {
    protected Method method;
    protected ModelMap modelMap;
    protected AbstractSectionSqlGen abstractWhereSqlGen;
    protected AbstractSectionSqlGen abstractSuffixSqlGen;

    protected static final String testTemplate = "%s.%s != null";

    public AbstractSqlGen(Method method, ModelMap modelMap) {
        this.method = method;
        this.modelMap = modelMap;
    }

    protected abstract SqlNode generateBaseSql();

    protected SqlNode generateWhereSql() {
        if (abstractWhereSqlGen == null) {
            return new StaticTextSqlNode("");
        }
        return abstractWhereSqlGen.generateSql();
    }

    protected SqlNode generateSuffixSql() {
        if (abstractSuffixSqlGen == null) {
            return new StaticTextSqlNode("");
        }
        return abstractSuffixSqlGen.generateSql();
    }

    @Override
    public MappedStatement generate() {
        MixedSqlNode rootSqlNode = new MixedSqlNode(Arrays.asList(generateBaseSql(), generateWhereSql(), generateSuffixSql()));
        MappedStatement mappedStatement = new MappedStatement.Builder(buildKey(), new DynamicSqlSource(rootSqlNode), sqlType())
                .resultType(modelMap.getType())
                .build();
        return mappedStatement;
    }


    public abstract String sqlType();

    protected String buildKey() {
        return method.getClass().getName() + "." + method.getName();
    }
}
