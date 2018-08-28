package com.tg.async.test;

import com.github.mauricio.async.db.*;
import com.github.mauricio.async.db.mysql.MySQLConnection;
import com.github.mauricio.async.db.mysql.MySQLQueryResult;
import com.github.mauricio.async.db.mysql.util.CharsetMapper;
import com.tg.async.mysql.ScalaUtils;
import com.tg.async.mysql.VertxEventLoopExecutionContext;
import io.netty.buffer.PooledByteBufAllocator;
import io.vertx.core.Vertx;
import org.junit.Test;
import scala.Option;
import scala.collection.Map$;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.runtime.AbstractFunction1;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by twogoods on 2018/4/12.
 */
public class BaseSqlTest {
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

            if(asyncResult.failed()){
                asyncResult.cause().printStackTrace();
                latch.countDown();
            }

            Future<QueryResult> qr = asyncResult.result().sendQuery("insert into T_User(username) values('twogoods')");
            qr.onComplete(ScalaUtils.toFunction1(ar -> {
                if (ar.succeeded()) {
                    QueryResult queryResult = ar.result();
                    System.out.println("rowsAffected: " + queryResult.rowsAffected());
                    System.out.println("insert id: " + ((MySQLQueryResult) queryResult).lastInsertId());

                    System.out.println(queryResult.rows().get().columnNames().toList());
                    queryResult.rows().get().foreach(new AbstractFunction1<RowData, Void>() {
                        @Override
                        public Void apply(RowData row) {
                            row.foreach(new AbstractFunction1<Object, Void>() {
                                @Override
                                public Void apply(Object value) {
                                    System.out.println("value" + value);
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
