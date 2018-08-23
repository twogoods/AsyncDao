package com.tg.async.dynamic.annotation.primary;

import com.tg.async.annotation.Count;
import com.tg.async.annotation.ModelConditions;
import com.tg.async.dynamic.annotation.AbstractSqlGen;
import com.tg.async.dynamic.annotation.suffix.ModelSuffixGen;
import com.tg.async.dynamic.annotation.suffix.ParamSuffixGen;
import com.tg.async.dynamic.annotation.where.FlatParamWhereSqlGen;
import com.tg.async.dynamic.annotation.where.ModelWhereSqlGen;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/17.
 */
public class Countgen extends AbstractSqlGen {
    private Count count;

    public Countgen(Method method, ModelMap modelMap, Count count) {
        super(method, modelMap);
        this.count = count;
        ModelConditions modelConditions = method.getAnnotation(ModelConditions.class);
        if (modelConditions != null) {
            this.abstractWhereSqlGen = new ModelWhereSqlGen(method, modelMap, modelConditions, count.sqlMode());
            this.abstractSuffixSqlGen = new ModelSuffixGen(method, modelMap, count.sqlMode());
        } else {
            this.abstractWhereSqlGen = new FlatParamWhereSqlGen(method, modelMap, count.sqlMode());
            this.abstractSuffixSqlGen = new ParamSuffixGen(method, modelMap, count.sqlMode());
        }
    }


    @Override
    protected SqlNode generateBaseSql() {
        return new StaticTextSqlNode("select count(*) from " + modelMap.getTable() + " ");
    }

    @Override
    public String sqlType() {
        return "select";
    }
}
