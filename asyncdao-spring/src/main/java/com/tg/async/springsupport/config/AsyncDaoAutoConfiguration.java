package com.tg.async.springsupport.config;

import com.tg.async.mysql.AsyncConfig;
import com.tg.async.mysql.AsyncDaoFactory;
import com.tg.async.mysql.pool.PoolConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by twogoods on 2018/8/27.
 */
@Configuration
@EnableConfigurationProperties(AsyncDaoConfig.class)
public class AsyncDaoAutoConfiguration {

    private final AsyncDaoConfig asyncDaoConfig;

    public AsyncDaoAutoConfiguration(AsyncDaoConfig asyncDaoConfig) {
        this.asyncDaoConfig = asyncDaoConfig;
    }

    @Bean
    public AsyncDaoFactory asyncDaoFactory() throws Exception {
        AsyncConfig asyncConfig = new AsyncConfig();
        PoolConfiguration configuration = new PoolConfiguration(asyncDaoConfig.getUsername(), asyncDaoConfig.getHost(), asyncDaoConfig.getPort(), asyncDaoConfig.getPassword(), asyncDaoConfig.getDatabase());
        asyncConfig.setPoolConfiguration(configuration);
        asyncConfig.setMapperPackages(asyncDaoConfig.getBasePackages());
        asyncConfig.setXmlLocations(asyncDaoConfig.getMapperLocations());
        return AsyncDaoFactory.build(asyncConfig);
    }
}
