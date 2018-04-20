package com.tg.async.mysql;

import com.tg.async.mysql.pool.PoolConfiguration;
import lombok.Data;

/**
 * Created by twogoods on 2018/4/20.
 */
@Data
public class AsyncConfig {
    private String mapperPackages;
    private String xmlLocations;
    private PoolConfiguration poolConfiguration;
}
