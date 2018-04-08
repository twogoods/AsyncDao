package com.tg.async.mysql.pool;

import com.github.mauricio.async.db.Configuration;
import com.github.mauricio.async.db.Connection;
import com.github.mauricio.async.db.mysql.MySQLConnection;
import com.github.mauricio.async.db.mysql.util.CharsetMapper;
import com.tg.async.mysql.VertxEventLoopExecutionContext;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by twogoods on 2018/4/8.
 */
public class ConnectionFactory extends BasePooledObjectFactory<Connection> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);
    protected final Configuration configuration;
    protected final Vertx vertx;

    public ConnectionFactory(Configuration configuration, Vertx vertx) {
        this.configuration = configuration;
        this.vertx = vertx;
    }

    @Override
    public Connection create() throws Exception {
        Connection connection = new MySQLConnection(configuration,
                CharsetMapper.Instance(),
                vertx.nettyEventLoopGroup().next(),
                VertxEventLoopExecutionContext.create(vertx));
        return connection;
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }


    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        p.getObject().disconnect();
    }

    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        return p.getObject().isConnected();
    }
}
