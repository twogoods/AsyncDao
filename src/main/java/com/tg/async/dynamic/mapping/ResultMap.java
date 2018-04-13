package com.tg.async.dynamic.mapping;

import lombok.Data;

import java.util.List;

/**
 * Created by twogoods on 2018/4/13.
 */
@Data
public class ResultMap {
    private String id;
    private String type;
    private Class clazz;
    private ResultMapping idResultMap;
    private List<ResultMapping> resultMappings;
}
