package com.tg.async.test;

import com.github.mauricio.async.db.*;
import com.github.mauricio.async.db.mysql.MySQLConnection;
import com.github.mauricio.async.db.mysql.util.CharsetMapper;
import com.tg.async.mapper.CommonDao;
import com.tg.async.base.DataHandler;
import com.tg.async.mapper.User;
import com.tg.async.mysql.AsyncConfig;
import com.tg.async.mysql.AsyncDaoFactory;
import com.tg.async.mysql.ScalaUtils;
import com.tg.async.mysql.VertxEventLoopExecutionContext;
import com.tg.async.mysql.pool.ConnectionPool;
import com.tg.async.mysql.pool.PoolConfiguration;
import io.netty.buffer.PooledByteBufAllocator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import org.junit.Test;
import scala.Option;
import scala.collection.Map$;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.runtime.AbstractFunction1;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by twogoods on 2018/4/12.
 */
public class BaseSqlTest {

    @Test
    public void test() throws Exception {

        Vertx vertx = Vertx.vertx();
        AsyncConfig asyncConfig = new AsyncConfig();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        asyncConfig.setPoolConfiguration(configuration);
        asyncConfig.setXmlLocations("");
        AsyncDaoFactory asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);

        CommonDao commonDao = asyncDaoFactory.getMapper(CommonDao.class);
        User user = new User();
        user.setUsername("ha");
        commonDao.query(user, new DataHandler<List<User>>() {
            @Override
            public void handle(List<User> users) {
                System.out.println(users);
            }
        });

        Thread.currentThread().join();
    }


    @Test
    public void base() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Vertx vertx = Vertx.vertx();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        ConnectionPool pool = new ConnectionPool(configuration, vertx);
        pool.getConnection(res -> {
            if (res.succeeded()) {

                res.result().queryWithParams("select * from T_User order by id desc", new ArrayList(), new Handler<AsyncResult<ResultSet>>() {
                    @Override
                    public void handle(AsyncResult<ResultSet> event) {
                        if (event.succeeded()) {
                            event.result().foreach(new AbstractFunction1<RowData, Void>() {
                                @Override
                                public Void apply(RowData row) {

                                    row.foreach(new AbstractFunction1<Object, Void>() {
                                        @Override
                                        public Void apply(Object value) {
                                            System.out.println(value);
                                            return null;
                                        }
                                    });
                                    latch.countDown();
                                    return null;
                                }
                            });
                        } else {
                            event.cause().printStackTrace();
                        }

                    }
                });
            } else {
                res.cause().printStackTrace();
            }
        });

        latch.await();
    }


    @Test
    public void origin() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Vertx vertx = Vertx.vertx();

        Configuration configuration = new Configuration(
                "root",
                "localhost",
                3306,
                Option.apply("admin"),
                Option.apply("test"),
                SSLConfiguration.apply(Map$.MODULE$.empty()),
                Charset.forName("UTF-8"),
                16777216,
                PooledByteBufAllocator.DEFAULT,
                Duration.apply(10000, TimeUnit.MILLISECONDS),
                Duration.apply(10000, TimeUnit.MILLISECONDS),
                Option.apply(Duration.apply(10000, TimeUnit.MILLISECONDS)));

        Connection connection = new MySQLConnection(configuration,
                CharsetMapper.Instance(),
                vertx.nettyEventLoopGroup().next(),
                VertxEventLoopExecutionContext.create(vertx));

        System.out.println(connection.isConnected());

        connection.connect().onComplete(ScalaUtils.toFunction1(asyncResult -> {
            Future<QueryResult> qr = asyncResult.result().sendQuery("select * from T_user");
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
                            latch.countDown();
                            return null;
                        }
                    });
                } else {
                    ar.cause().printStackTrace();
                }
            }), VertxEventLoopExecutionContext.create(vertx));

        }), VertxEventLoopExecutionContext.create(vertx));

        latch.await();
    }


}
