package com.tg.async.mysql;

import com.github.mauricio.async.db.ResultSet;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import java.util.List;


/**
 * Created by twogoods on 2018/4/8.
 */
public interface SQLConnection {
    SQLConnection call(String sql, Handler<AsyncResult<ResultSet>> resultHandler);

    SQLConnection callWithParams(String sql, JsonArray params, JsonArray outputs, Handler<AsyncResult<ResultSet>> resultHandler);

    SQLConnection setAutoCommit(boolean autoCommit, Handler<AsyncResult<Void>> handler);

    SQLConnection execute(String sql, Handler<AsyncResult<Void>> handler);

    SQLConnection query(String sql, Handler<AsyncResult<ResultSet>> handler);

    SQLConnection queryWithParams(String sql, JsonArray params, Handler<AsyncResult<ResultSet>> handler);

    SQLConnection update(String sql, Handler<AsyncResult<ResultSet>> handler);

    SQLConnection updateWithParams(String sql, JsonArray params, Handler<AsyncResult<ResultSet>> handler);

    void close(Handler<AsyncResult<Void>> handler);

    void close();

    SQLConnection commit(Handler<AsyncResult<Void>> handler);

    SQLConnection rollback(Handler<AsyncResult<Void>> handler);

    SQLConnection setTransactionIsolation(TransactionIsolation transactionIsolation, Handler<AsyncResult<Void>> handler);

    SQLConnection getTransactionIsolation(Handler<AsyncResult<TransactionIsolation>> handler);

    SQLConnection batch(List<String> sqlStatements, Handler<AsyncResult<List<Integer>>> handler);

    SQLConnection batchWithParams(String sqlStatement, List<JsonArray> args, Handler<AsyncResult<List<Integer>>> handler);

    SQLConnection batchCallableWithParams(String sqlStatement, List<JsonArray> inArgs, List<JsonArray> outArgs, Handler<AsyncResult<List<Integer>>> handler);

}
