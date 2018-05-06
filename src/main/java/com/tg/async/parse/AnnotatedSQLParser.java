package com.tg.async.parse;

import com.tg.async.annotation.Select;
import com.tg.async.dynamic.annotation.SelectGen;
import com.tg.async.dynamic.annotation.SqlGen;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.mysql.Configuration;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/6.
 */
public class AnnotatedSQLParser {

    private Configuration configuration;
    private String className;

    public AnnotatedSQLParser(Configuration configuration, String className) {
        this.configuration = configuration;
        this.className = className;
    }

    public void parse(Method method) {
        SqlGen sqlGen = prepare(method);
        configuration.addMappedStatement(buildKey(className, method.getName()), sqlGen.generate());
    }


    private SqlGen prepare(Method method) {
        ModelMap modelMap = configuration.getModelMap(className);
        Select select = method.getAnnotation(Select.class);
        if (select != null) {
            return new SelectGen(method, select, modelMap);
        }
        return null;
    }

    private String buildKey(String className, String methodName) {
        return className + "." + methodName;
    }
}
