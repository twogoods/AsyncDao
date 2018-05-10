package com.tg.async.dynamic.annotation.primary;

import com.tg.async.annotation.*;
import com.tg.async.dynamic.annotation.AbstractSqlGen;
import com.tg.async.dynamic.annotation.where.FlatParamWhereSqlGen;
import com.tg.async.dynamic.annotation.where.ModelWhereSqlGen;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/6.
 */
public class SelectGen extends AbstractSqlGen {
    private Select select;

    public SelectGen(Method method, Select select, ModelMap modelMap) {
        super(method, modelMap);
        this.select = select;
        ModelConditions modelConditions = method.getAnnotation(ModelConditions.class);
        if (modelConditions != null) {
            this.abstractWhereSqlGen = new ModelWhereSqlGen(method, modelMap, modelConditions);
        } else {
            this.abstractWhereSqlGen = new FlatParamWhereSqlGen(method, modelMap);
        }
    }

    @Override
    public SqlNode generateBaseSql() {
        String column = StringUtils.isEmpty(select.columns()) ? "*" : select.columns();
        String data = String.format("select %s from %s ", column, modelMap.getTable());
        return new StaticTextSqlNode(data);
    }
}
