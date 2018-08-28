package com.tg.async.springsupport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by twogoods on 2018/8/27.
 */
@Data
@ConfigurationProperties(prefix = AsyncDaoConfig.CONF_PREFIX)
public class AsyncDaoConfig {
    public static final String CONF_PREFIX = "async.dao";

    private String mapperLocations;

    private String basePackages;

    private String username;

    private String host;

    private int port;

    private String password;

    private String database;

    private int maxTotal=12;
    private int maxIdle=12;
    private int minIdle=1;
    private long maxWaitMillis=10000L;

}
