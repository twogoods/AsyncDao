package com.tg.async.dynamic.annotation.where;

import com.tg.async.dynamic.mapping.ModelMap;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/10.
 */
public class FlatParamWhereSqlGen extends AbstractWhereSqlGen{


    public FlatParamWhereSqlGen(Method method, ModelMap modelMap) {
        super(method, modelMap);
    }
}
