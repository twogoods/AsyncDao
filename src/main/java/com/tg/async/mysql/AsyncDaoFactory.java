package com.tg.async.mysql;

import com.tg.async.mysql.pool.PoolConfiguration;
import com.tg.async.proxy.MapperProxyFactory;

/**
 * Created by twogoods on 2018/4/12.
 */
public class AsyncDaoFactory {

    private PoolConfiguration configuration;

    private String basePackage;
    private String mapperLocations;


    private AsyncDaoFactory(PoolConfiguration configuration) {
        this.configuration = configuration;
    }


    public <T> T getMapper(Class<T> type) {
        return new MapperProxyFactory<T>(type).newInstance();
    }


    public static AsyncDaoFactory build(PoolConfiguration configuration) {
        return new AsyncDaoFactory(configuration);
    }
}
