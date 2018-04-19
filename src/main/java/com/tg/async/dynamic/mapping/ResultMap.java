package com.tg.async.dynamic.mapping;


import java.util.List;

/**
 * Created by twogoods on 2018/4/13.
 */
public class ResultMap {
    private String id;
    private String type;
    private Class clazz;
    private ResultMapping idResultMap;
    private List<ResultMapping> resultMappings;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public ResultMapping getIdResultMap() {
        return idResultMap;
    }

    public void setIdResultMap(ResultMapping idResultMap) {
        this.idResultMap = idResultMap;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public void setResultMappings(List<ResultMapping> resultMappings) {
        this.resultMappings = resultMappings;
    }
}
