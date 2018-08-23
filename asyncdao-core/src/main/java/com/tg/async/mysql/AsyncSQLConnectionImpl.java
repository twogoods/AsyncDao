package com.tg.async.mysql;

import com.github.mauricio.async.db.Connection;
import com.github.mauricio.async.db.QueryResult;
import com.github.mauricio.async.db.ResultSet;
import com.tg.async.mysql.pool.ConnectionPool;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import scala.concurrent.ExecutionContext;

import java.util.List;

public class AsyncSQLConnectionImpl implements SQLConnection {

    private final ExecutionContext executionContext;
    private volatile boolean inTransaction = false;
    private boolean inAutoCommit = true;

    private final Connection connection;
    private final ConnectionPool pool;

    public AsyncSQLConnectionImpl(Connection connection, ConnectionPool pool, ExecutionContext executionContext) {
        this.connection = connection;
        this.pool = pool;
        this.executionContext = executionContext;
    }

    @Override
    public SQLConnection setAutoCommit(boolean autoCommit, Handler<AsyncResult<Void>> handler) {
        Future<Void> fut;
        synchronized (this) {
            if (inTransaction && autoCommit) {
                inTransaction = false;
                fut = ScalaUtils.scalaToVertxVoid(connection.sendQuery("COMMIT"), executionContext);
            } else {
                fut = Future.succeededFuture();
            }
            inAutoCommit = autoCommit;
        }
        fut.setHandler(handler);
        return this;
    }

    @Override
    public SQLConnection execute(String sql, Handler<AsyncResult<Void>> handler) {
        beginTransactionIfNeeded(v -> {
            final scala.concurrent.Future<QueryResult> future = connection.sendQuery(sql);
            future.onComplete(ScalaUtils.toFunction1(ar -> {
                if (ar.succeeded()) {
                    handler.handle(Future.succeededFuture());
                } else {
                    handler.handle(Future.failedFuture(ar.cause()));
                }
            }), executionContext);
        });
        return this;
    }

    @Override
    public SQLConnection executeWithParams(String sql, List params, Handler<AsyncResult<Void>> handler) {
        beginTransactionIfNeeded(v -> {
            final scala.concurrent.Future<QueryResult> future = connection.sendPreparedStatement(sql, ScalaUtils.toScalaList(params));
            future.onComplete(ScalaUtils.toFunction1(ar -> {
                if (ar.succeeded()) {
                    handler.handle(Future.succeededFuture());
                } else {
                    handler.handle(Future.failedFuture(ar.cause()));
                }
            }), executionContext);
        });
        return this;
    }

    @Override
    public SQLConnection query(String sql, Handler<AsyncResult<QueryResult>> handler) {
        beginTransactionIfNeeded(v -> {
            final Future<QueryResult> future = ScalaUtils.scalaToVertx(connection.sendQuery(sql), executionContext);
            future.setHandler(handler);
        });
        return this;
    }

    @Override
    public SQLConnection queryWithParams(String sql, List params, Handler<AsyncResult<QueryResult>> handler) {
        beginTransactionIfNeeded(v -> {
            final scala.concurrent.Future<QueryResult> future = connection.sendPreparedStatement(sql, ScalaUtils.toScalaList(params));
            future.onComplete(ScalaUtils.toFunction1(handler), executionContext);
        });
        return this;
    }


    @Override
    public SQLConnection update(String sql, Handler<AsyncResult<QueryResult>> handler) {
        beginTransactionIfNeeded(v -> {
            final scala.concurrent.Future<QueryResult> future = connection.sendQuery(sql);
            future.onComplete(ScalaUtils.toFunction1(handler), executionContext);
        });
        return this;
    }

    @Override
    public SQLConnection updateWithParams(String sql, List params, Handler<AsyncResult<QueryResult>> handler) {
        beginTransactionIfNeeded(v -> {
            final scala.concurrent.Future<QueryResult> future = connection.sendPreparedStatement(sql, ScalaUtils.toScalaList(params));
            future.onComplete(ScalaUtils.toFunction1(handler), executionContext);
        });
        return this;
    }

    @Override
    public synchronized void close(Handler<AsyncResult<Void>> handler) {
        inAutoCommit = true;
        if (inTransaction) {
            inTransaction = false;
            Future<QueryResult> future = ScalaUtils.scalaToVertx(connection.sendQuery("COMMIT"), executionContext);
            future.setHandler((v) -> {
                pool.returnObject(connection);
                handler.handle(Future.succeededFuture());
            });
        } else {
            pool.returnObject(connection);
            handler.handle(Future.succeededFuture());
        }
    }

    @Override
    public void close() {
        close((ar) -> {
            // Do nothing by default.
        });
    }

    @Override
    public SQLConnection commit(Handler<AsyncResult<Void>> handler) {
        return endAndStartTransaction("COMMIT", handler);
    }

    @Override
    public SQLConnection rollback(Handler<AsyncResult<Void>> handler) {
        return endAndStartTransaction("ROLLBACK", handler);
    }

    @Override
    public SQLConnection setTransactionIsolation(TransactionIsolation transactionIsolation, Handler<AsyncResult<Void>> handler) {
        String sql;
        switch (transactionIsolation) {
            case READ_UNCOMMITTED:
                sql = "SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
                break;
            case REPEATABLE_READ:
                sql = "SET TRANSACTION ISOLATION LEVEL REPEATABLE READ";
                break;
            case READ_COMMITTED:
                sql = "SET TRANSACTION ISOLATION LEVEL READ COMMITTED";
                break;
            case SERIALIZABLE:
                sql = "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE";
                break;
            case NONE:
            default:
                sql = null;
                break;
        }

        if (sql == null) {
            handler.handle(Future.succeededFuture());
            return this;
        }

        return execute(sql, handler);
    }

    @Override
    public SQLConnection getTransactionIsolation(Handler<AsyncResult<TransactionIsolation>> handler) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private SQLConnection endAndStartTransaction(String command, Handler<AsyncResult<Void>> handler) {
        if (inTransaction) {
            inTransaction = false;
            ScalaUtils.scalaToVertx(connection.sendQuery(command), executionContext).setHandler(
                    ar -> {
                        if (ar.failed()) {
                            handler.handle(Future.failedFuture(ar.cause()));
                        } else {
                            ScalaUtils.scalaToVertx(connection.sendQuery("BEGIN"), executionContext).setHandler(
                                    ar2 -> {
                                        if (ar2.failed()) {
                                            handler.handle(Future.failedFuture(ar.cause()));
                                        } else {
                                            inTransaction = true;
                                            handler.handle(Future.succeededFuture());
                                        }
                                    }
                            );
                        }
                    });
        } else {
            handler.handle(Future.failedFuture(
                    new IllegalStateException("Not in transaction currently")));
        }
        return this;
    }

    private synchronized void beginTransactionIfNeeded(Handler<AsyncResult<Void>> action) {
        if (!inAutoCommit && !inTransaction) {
            inTransaction = true;
            ScalaUtils.scalaToVertxVoid(connection.sendQuery("BEGIN"), executionContext)
                    .setHandler(action);
        } else {
            action.handle(Future.succeededFuture());
        }
    }

    private Handler<AsyncResult<QueryResult>> handleAsyncQueryResultToResultSet(Handler<AsyncResult<ResultSet>> handler) {
        return ar -> {
            if (ar.succeeded()) {
                try {
                    handler.handle(Future.succeededFuture(ar.result().rows().get()));
                } catch (Throwable e) {
                    handler.handle(Future.failedFuture(e));
                }
            } else {
                handler.handle(Future.failedFuture(ar.cause()));
            }
        };
    }
}
