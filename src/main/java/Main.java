import com.github.mauricio.async.db.QueryResult;
import com.github.mauricio.async.db.RowData;
import com.tg.async.mysql.ScalaUtils;
import com.tg.async.mysql.VertxEventLoopExecutionContext;
import com.tg.async.mysql.pool.ConnectionPool;
import com.tg.async.mysql.pool.PoolConfiguration;
import io.vertx.core.Vertx;
import scala.concurrent.Future;
import scala.runtime.AbstractFunction1;

/**
 * Created by twogoods on 2018/4/8.
 */
public class Main {
    public static void main(String[] args) {
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
