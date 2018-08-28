package com.tg.async.mysql;

import com.tg.async.mysql.pool.PoolConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by twogoods on 2018/4/20.
 */
public class AsyncConfig {
    private List<String> mapperPackages;
    private List<String> xmlLocations;
    private PoolConfiguration poolConfiguration;


    public List<String> getMapperPackages() {
        return mapperPackages;
    }

    public void setMapperPackages(List<String> mapperPackages) {
        this.mapperPackages = mapperPackages;
    }

    public void setMapperPackages(String mapperPackages) {
        if (StringUtils.isNotEmpty(mapperPackages)) {
            this.mapperPackages = Arrays.asList(mapperPackages.split(","));
        }
    }

    public List<String> getXmlLocations() {
        return xmlLocations;
    }

    public void setXmlLocations(List<String> xmlLocations) {
        this.xmlLocations = xmlLocations;
    }

    public void setXmlLocations(String xmlLocations) {
        if (StringUtils.isNotEmpty(xmlLocations)) {
            this.xmlLocations = Arrays.asList(xmlLocations.split(","));
        }
    }

    public PoolConfiguration getPoolConfiguration() {
        return poolConfiguration;
    }

    public void setPoolConfiguration(PoolConfiguration poolConfiguration) {
        this.poolConfiguration = poolConfiguration;
    }
}
