package com.tg.async.dynamic.mapping;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by twogoods on 2018/4/13.
 */
@Data
public class ModelMap {
    private String id;
    private String type;
    private String table;
    private Class clazz;
    private ColumnMapping idResultMap;
    private Map<String, ColumnMapping> columnKeyMappings;
    private Map<String, ColumnMapping> fieldKeyMappings;


    public String getType() {
        if (StringUtils.isEmpty(type)) {
            return clazz.getName();
        }
        return type;
    }
}
