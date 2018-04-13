package com.tg.async;

import com.tg.async.mapper.CommonDao;
import com.tg.async.base.DataHandler;
import com.tg.async.mapper.User;
import com.tg.async.mysql.AsyncDaoFactory;
import com.tg.async.mysql.pool.ConnectionPool;
import com.tg.async.mysql.pool.PoolConfiguration;
import io.vertx.core.Vertx;
import org.junit.Test;

import java.util.List;

/**
 * Created by twogoods on 2018/4/12.
 */
public class Base {


    @Test
    public void test() {
        Vertx vertx = Vertx.vertx();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        AsyncDaoFactory asyncDaoFactory = AsyncDaoFactory.build(configuration);
        CommonDao commonDao = asyncDaoFactory.getMapper(CommonDao.class);

        commonDao.query(new User(), new DataHandler<List<User>>() {
            @Override
            public void handle(List<User> users) {
                System.out.println(users);
            }
        });

    }


    @Test
    public void base() {
        Vertx vertx = Vertx.vertx();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        ConnectionPool pool = new ConnectionPool(configuration, vertx);
        pool.getConnection(res -> {
            if (res.succeeded()) {
                /*
                Future<QueryResult> qr = res.result().sendQuery("select * from T_user");
                qr.onComplete(ScalaUtils.toFunction1(ar -> {
                    if (ar.succeeded()) {
                        QueryResult queryResult = ar.result();

                        System.out.println(queryResult.rows().get().columnNames().toList());

                        queryResult.rows().get().foreach(new AbstractFunction1<RowData, Void>() {
                            @Override
                            public Void apply(RowData row) {

                                row.foreach(new AbstractFunction1<Object, Void>() {
                                    @Override
                                    public Void apply(Object value) {
                                        System.out.println(value);
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });

                    } else {
                        ar.cause().printStackTrace();
                    }
                }), VertxEventLoopExecutionContext.create(vertx));
                */
            } else {
                res.cause().printStackTrace();
            }
        });
    }


}
