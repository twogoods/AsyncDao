package com.tg.async.parse;

import com.tg.async.annotation.Delete;
import com.tg.async.annotation.Insert;
import com.tg.async.annotation.Select;
import com.tg.async.annotation.Update;
import com.tg.async.dynamic.annotation.primary.DeleteGen;
import com.tg.async.dynamic.annotation.primary.InsertGen;
import com.tg.async.dynamic.annotation.primary.SelectGen;
import com.tg.async.dynamic.annotation.SqlGen;
import com.tg.async.dynamic.annotation.primary.UpdateGen;
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
        if (sqlGen != null) {
            configuration.addMappedStatement(buildKey(className, method.getName()), sqlGen.generate());
        }
    }


    private SqlGen prepare(Method method) {
        ModelMap modelMap = configuration.getModelMap(className);
        Select select = method.getAnnotation(Select.class);
        if (select != null) {
            return new SelectGen(method, select, modelMap);
        }
        Insert insert = method.getAnnotation(Insert.class);
        if (insert != null) {
            return new InsertGen(method, insert, modelMap);
        }
        Update update = method.getAnnotation(Update.class);
        if (update != null) {
            return new UpdateGen(method, modelMap, update);
        }
        Delete delete = method.getAnnotation(Delete.class);
        if (delete != null) {
            return new DeleteGen(method, modelMap, delete);
        }
        return null;
    }

    private String buildKey(String className, String methodName) {
        return className + "." + methodName;
    }
}
