package com.tg.async.mysql;

import com.tg.async.proxy.MapperProxyFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by twogoods on 2018/8/23.
 */
public class Translaction {

    private Configuration configuration;
    private SQLConnection connection;


    public Translaction(Configuration configuration, SQLConnection connection) {
        this.configuration = configuration;
        this.connection = connection;
    }

    public <T> T getMapper(Class<T> type) {
        return new MapperProxyFactory<T>(type).newInstance(configuration, connection);
    }


    public void rollback(Handler<AsyncResult<Void>> handler) {
        connection.rollback(handler);
    }

    public void commit(Handler<AsyncResult<Void>> handler) {
        connection.commit(handler);
    }

    public void close() {
        connection.close();
    }


}
