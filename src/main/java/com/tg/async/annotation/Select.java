package com.tg.async.annotation;


import com.tg.async.constant.SqlMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by twogoods on 2018/4/12.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Select {
    String columns() default "";

    SqlMode sqlMode() default SqlMode.SELECTIVE;
}
