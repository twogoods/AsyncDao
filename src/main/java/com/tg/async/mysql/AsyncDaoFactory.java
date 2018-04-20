package com.tg.async.mysql;

import com.tg.async.mysql.pool.PoolConfiguration;
import com.tg.async.proxy.MapperProxyFactory;
import io.vertx.core.Vertx;
/**
 * Created by twogoods on 2018/4/12.
 */
public class AsyncDaoFactory {

    private PoolConfiguration configuration;

    private AsyncDaoFactory(PoolConfiguration configuration) {
        this.configuration = configuration;
    }

    public <T> T getMapper(Class<T> type) {
        return new MapperProxyFactory<T>(type).newInstance();
    }

    public static AsyncDaoFactory build(AsyncConfig config, Vertx vertx) throws Exception {
        new MapperLoader().load(config);
        return new AsyncDaoFactory(config.getPoolConfiguration());
    }

    public static AsyncDaoFactory build(AsyncConfig config) throws Exception {
        return build(config, null);
    }
}
