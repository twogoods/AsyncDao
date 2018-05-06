package com.tg.async.dynamic.mapping;


import lombok.Data;

/**
 * Created by twogoods on 2018/4/13.
 */
@Data
public class ColumnMapping {
    private String column;
    private String property;

    public ColumnMapping() {
    }

    public ColumnMapping(String column, String property) {
        this.column = column;
        this.property = property;
    }
}
