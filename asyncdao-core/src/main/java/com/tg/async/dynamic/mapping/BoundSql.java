package com.tg.async.dynamic.mapping;

import lombok.Data;

import java.util.List;

/**
 * Created by twogoods on 2018/4/19.
 */
@Data
public class BoundSql {
    private String sql;
    private List<Object> parameters;

    public BoundSql() {
    }

    public BoundSql(String sql, List<Object> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }
}
