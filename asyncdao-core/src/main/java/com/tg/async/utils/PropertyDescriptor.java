package com.tg.async.utils;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.util.Locale.ENGLISH;

/**
 * Created by twogoods on 2018/5/2.
 */
@Slf4j
public class PropertyDescriptor {
    private Class clazz;
    private String name;
    private Class type;
    private Method setter;

    public PropertyDescriptor(Class clazz, String name) {
        this.clazz = clazz;
        this.name = name;
        try {
            type = clazz.getDeclaredField(name).getType();
            setter = clazz.getDeclaredMethod(getSetterName(), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setValue(Object object, Object value) throws Exception {
        try {
            setter.invoke(object, new Object[]{value});
        } catch (IllegalArgumentException e) {
            log.error("field {} is {},but data is {}, check class definition in {}", name, type, value.getClass(), clazz.getName());
        }
    }


    //TODO mysql类型与返回参数类型,先按异步驱动自己的规定
    private Object convertValue(Object value) {
        if (value.getClass().equals(LocalDateTime.class)) {
            return Timestamp.valueOf((LocalDateTime) value);
        }

        if (value.getClass().equals(LocalDate.class)) {
            return ((LocalDate) value).toDate();
        }
        return value;
    }


    private String getSetterName() {
        if (name == null || name.length() == 0) {
            return name;
        }
        return "set" + name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }
}
