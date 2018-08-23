package com.tg.async.dynamic.annotation.primary;

import com.tg.async.annotation.Delete;
import com.tg.async.annotation.ModelConditions;
import com.tg.async.dynamic.annotation.AbstractSqlGen;
import com.tg.async.dynamic.annotation.where.FlatParamWhereSqlGen;
import com.tg.async.dynamic.annotation.where.ModelWhereSqlGen;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/11.
 */
public class DeleteGen extends AbstractSqlGen {

    private Delete delete;

    public DeleteGen(Method method, ModelMap modelMap, Delete delete) {
        super(method, modelMap);
        this.delete = delete;

        ModelConditions modelConditions = method.getAnnotation(ModelConditions.class);
        if (modelConditions != null) {
            this.abstractWhereSqlGen = new ModelWhereSqlGen(method, modelMap, modelConditions, delete.sqlMode());
        } else {
            this.abstractWhereSqlGen = new FlatParamWhereSqlGen(method, modelMap, delete.sqlMode());
        }

    }

    @Override
    public SqlNode generateBaseSql() {
        return new StaticTextSqlNode("delete from " + modelMap.getTable());
    }

    @Override
    public String sqlType() {
        return "delete";
    }
}
