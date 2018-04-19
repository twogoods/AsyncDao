package com.tg.async.dynamic.mapping;

import lombok.Data;

/**
 * Created by twogoods on 2018/4/19.
 */
@Data
public class BoundSql {
    private String sql;
    private Object parameterObject;
}
