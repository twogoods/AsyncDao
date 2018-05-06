package com.tg.async.dynamic.annotation;

import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.MixedSqlNode;

/**
 * Created by twogoods on 2018/5/6.
 */
public abstract class AbstractSqlGen implements SqlGen {
    protected ModelMap modelMap;

    public AbstractSqlGen(ModelMap modelMap) {
        this.modelMap = modelMap;
    }


    abstract void generateBasePart();


    @Override
    public MappedStatement generate() {
        MixedSqlNode rootSqlNode;
        generateBasePart();
        return null;
    }
}
