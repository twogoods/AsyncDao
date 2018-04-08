package com.tg.async.mysql.pool;

import com.github.mauricio.async.db.Configuration;
import com.github.mauricio.async.db.SSLConfiguration;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.StringUtil;
import scala.Option;
import scala.Tuple2;
import scala.collection.Map$;
import scala.collection.immutable.Map;
import scala.concurrent.duration.Duration;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by twogoods on 2018/4/8.
 */
public class PoolConfiguration {
    private String username;
    private String host;
    private int port;
    private String password;
    private String database;
    private SSLConfiguration ssl;
    private Charset charset = Charset.forName("UTF-8");
    private int maximumMessageSize = 16777216;
    private ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
    private Long connectTimeout = 10000L;
    private Long testTimeout = 10000L;
    private Long queryTimeout = 120 * 1000L;


    private String sslMode;
    private String sslRootCert;


    private int maxTotal=12;
    private int maxIdle=12;
    private int minIdle=1;
    private long borrowMaxWaitMillis=10000L;


    public PoolConfiguration(String username, String host, int port, String password, String database) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.password = password;
        this.database = database;
    }

    public Configuration getConfig() {
        return new Configuration(
                username,
                host,
                port,
                Option.apply(password),
                Option.apply(database),
                SSLConfiguration.apply(buildSslConfig()),
                charset,
                16777216,
                PooledByteBufAllocator.DEFAULT,
                Duration.apply(connectTimeout, TimeUnit.MILLISECONDS),
                Duration.apply(testTimeout, TimeUnit.MILLISECONDS),
                Option.apply(Duration.apply(queryTimeout, TimeUnit.MILLISECONDS)));
    }


    private Map<String, String> buildSslConfig() {
        Map<String, String> sslConfig = Map$.MODULE$.empty();
        if (!StringUtil.isNullOrEmpty(sslMode)) {
            sslConfig = sslConfig.$plus(Tuple2.apply("sslmode", sslMode));
        }
        if (!StringUtil.isNullOrEmpty(sslRootCert)) {
            sslConfig = sslConfig.$plus(Tuple2.apply("sslrootcert", sslRootCert));
        }
        return sslConfig;
    }


    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }


    public long getBorrowMaxWaitMillis() {
        return borrowMaxWaitMillis;
    }
}
