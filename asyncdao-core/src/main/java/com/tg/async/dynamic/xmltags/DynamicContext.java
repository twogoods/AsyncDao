package com.tg.async.dynamic.xmltags;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/13.
 */
public class DynamicContext {
    private final StringBuilder sqlBuilder = new StringBuilder();

    private Object parameterObject;

    private Map<String, Object> bindParam = new HashMap();

    public DynamicContext() {
    }

    public DynamicContext(Object parameterObject) {
        this.parameterObject = parameterObject;
    }

    public void appendSql(String sql) {
        sqlBuilder.append(sql);
        sqlBuilder.append(" ");
    }

    public String getSql() {
        return sqlBuilder.toString();
    }

    public Object getParam() {
        return parameterObject;
    }


    public Map<String, Object> getBindParam() {
        return bindParam;
    }

    public void bind(String key, Object value) {
        bindParam.put(key, value);
    }

}
