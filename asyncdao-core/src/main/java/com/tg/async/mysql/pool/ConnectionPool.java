package com.tg.async.mysql.pool;

import com.github.mauricio.async.db.Connection;
import com.tg.async.mysql.SQLConnection;
import com.tg.async.mysql.ScalaUtils;
import com.tg.async.mysql.AsyncSQLConnectionImpl;
import com.tg.async.mysql.VertxEventLoopExecutionContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import scala.concurrent.ExecutionContext;

/**
 * Created by twogoods on 2018/4/8.
 */
public class ConnectionPool {

    private GenericObjectPool<Connection> pool;
    private final Vertx vertx;
    private long borrowMaxWaitMillis;
    private ExecutionContext executionContext;

    public ConnectionPool(PoolConfiguration configuration, Vertx vertx) {
        this.vertx = vertx;
        this.executionContext = VertxEventLoopExecutionContext.create(vertx);
        this.borrowMaxWaitMillis = configuration.getBorrowMaxWaitMillis();
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(configuration.getMaxTotal());
        config.setMaxIdle(configuration.getMaxIdle());
        config.setMinIdle(configuration.getMinIdle());
        config.setMaxWaitMillis(0);
        pool = new GenericObjectPool<>(new ConnectionFactory(configuration.getConfig(), vertx), config);
        try {
            pool.preparePool();
        } catch (Exception e) {
            throw new RuntimeException("{init connectionpool error: {}}", e);
        }
    }

    public void close() {
        pool.close();
    }

    public void getConnection(Handler<AsyncResult<SQLConnection>> handler) {
        Connection connection = null;
        try {
            connection = pool.borrowObject(borrowMaxWaitMillis);
        } catch (Exception e) {
            handler.handle(Future.failedFuture(e));
        }
        if (connection.isConnected()) {
            handler.handle(Future.succeededFuture(new AsyncSQLConnectionImpl(connection, this, executionContext)));
        } else {
            connection.connect().onComplete(ScalaUtils.toFunction1(asyncResult -> {
                handler.handle(Future.succeededFuture(new AsyncSQLConnectionImpl(asyncResult.result(), this, executionContext)));
            }), executionContext);
        }
    }

    public void returnObject(Connection connection) {
        pool.returnObject(connection);
    }
}
