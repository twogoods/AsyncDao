package com.tg.async.proxy;

import com.tg.async.mysql.Configuration;
import com.tg.async.mysql.SQLConnection;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Created by twogoods on 2018/8/23.
 */
public class TranslationMapperProxy extends MapperProxy{

    private SQLConnection connection;

    public TranslationMapperProxy(Configuration configuration, Class mapperInterface, SQLConnection connection) {
        super(configuration, mapperInterface);
        this.connection = connection;
    }

    @Override
    protected void getConnection(Handler handler) {
        handler.handle(Future.succeededFuture(connection));
    }
}
