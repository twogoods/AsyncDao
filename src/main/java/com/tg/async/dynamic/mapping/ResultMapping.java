package com.tg.async.dynamic.mapping;


/**
 * Created by twogoods on 2018/4/13.
 */

public class ResultMapping {
    private String column;
    private String property;


    public ResultMapping(String column, String property) {
        this.column = column;
        this.property = property;
    }
}
