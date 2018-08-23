package com.tg.async.dynamic.annotation;

import com.tg.async.dynamic.mapping.MappedStatement;

/**
 * Created by twogoods on 2018/5/6.
 */
public interface SqlGen {
    MappedStatement generate();
}
