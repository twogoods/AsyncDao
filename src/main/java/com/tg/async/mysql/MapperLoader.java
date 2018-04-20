package com.tg.async.mysql;

import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.mysql.pool.ConnectionPool;
import com.tg.async.mysql.pool.PoolConfiguration;
import com.tg.async.utils.ResourceScanner;
import io.vertx.core.Vertx;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by twogoods on 2018/4/20.
 */
public class MapperLoader {
    public void load(AsyncConfig asyncConfig) throws Exception {
        parseXmlMapper(asyncConfig.getXmlLocations());
        parseIfaceMapper(asyncConfig.getMapperPackages());
    }

    private void parseXmlMapper(String path) throws Exception {
        Set<String> files = ResourceScanner.getXml(Arrays.asList(path.split(",")));
        for (String file : files) {
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(ResourceScanner.getStreamFromFile(file), file);
            xmlMapperBuilder.parse();
        }
    }

    private void parseIfaceMapper(String packageName) {

    }

    private void preparePool(PoolConfiguration configuration) {
        Vertx vertx = Vertx.vertx();
        ConnectionPool pool = new ConnectionPool(configuration, vertx);

    }
}