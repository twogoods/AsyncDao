package com.tg.async.parse;

import com.tg.async.base.MapperMethod;
import com.tg.async.exception.BuilderException;
import com.tg.async.mysql.Configuration;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/4/27.
 */
public class IfaceParser implements Parser {
    private Configuration configuration;
    private String className;

    public IfaceParser(Configuration configuration, String className) {
        this.configuration = configuration;
        this.className = className;
    }

    @Override
    public void parse() {
        try {
            Class clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                configuration.addMapperMethod(method, new MapperMethod(clazz, method));
            }
        } catch (ClassNotFoundException e) {
            throw new BuilderException(e);
        }
    }
}
