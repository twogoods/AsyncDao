package com.tg.async.dynamic.xmltags;

/**
 * Created by twogoods on 2018/4/13.
 */
public class DynamicContext {
    private final StringBuilder sqlBuilder = new StringBuilder();


    public void appendSql(String sql) {
        sqlBuilder.append(sql);
        sqlBuilder.append(" ");
    }
}
