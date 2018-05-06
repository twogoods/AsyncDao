package com.tg.async.dynamic.annotation;

import com.tg.async.annotation.*;
import com.tg.async.dynamic.mapping.ModelMap;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/6.
 */
public class SelectGen extends AbstractSqlGen {

    private Method method;
    private Select select;


    public SelectGen(Method method, Select select, ModelMap modelMap) {
        super(modelMap);
        this.method = method;
        this.select = select;
    }

    @Override
    public void generateBasePart() {
        String base = "select ";
    }
}
