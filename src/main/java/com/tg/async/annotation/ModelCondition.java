package com.tg.async.annotation;

import com.tg.async.constant.Attach;
import com.tg.async.constant.Criterions;
import com.tg.async.constant.InType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by twogoods on 2018/4/12.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelCondition {
    Criterions criterion() default Criterions.EQUAL;

    String field();

    String column() default "";

    Attach attach() default Attach.AND;

    String test() default "";

    //in 查询时设置
    InType paramType() default InType.COLLECTION;
}
