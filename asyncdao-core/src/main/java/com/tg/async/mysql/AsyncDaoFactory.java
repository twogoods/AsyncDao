package com.tg.async.mysql;

import com.tg.async.proxy.MapperProxyFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Created by twogoods on 2018/4/12.
 */
public class AsyncDaoFactory {
    private Configuration configuration;

    public AsyncDaoFactory() {
        this.configuration = new Configuration();
    }

    public <T> T getMapper(Class<T> type) {
        return new MapperProxyFactory<T>(type).newInstance(configuration);
    }

    public static AsyncDaoFactory build(AsyncConfig asyncConfig, Vertx vertx) throws Exception {
        AsyncDaoFactory asyncDaoFactory = new AsyncDaoFactory();
        new MapperLoader().load(asyncDaoFactory.getConfiguration(), asyncConfig, vertx);
        return asyncDaoFactory;
    }

    public static AsyncDaoFactory build(AsyncConfig config) throws Exception {
        return build(config, null);
    }

    public Configuration getConfiguration() {
        return configuration;
    }


    public void startTranslation(Handler<AsyncResult<Translaction>> handler) {
        configuration.getConnectionPool().getConnection(res -> {
            SQLConnection connection = res.result();
            connection.setAutoCommit(false, Void -> {
                handler.handle(Future.succeededFuture(new Translaction(configuration, res.result())));
            });
        });
    }
}
