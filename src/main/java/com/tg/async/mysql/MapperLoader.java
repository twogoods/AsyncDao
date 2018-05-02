package com.tg.async.mysql;

import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.mysql.pool.ConnectionPool;
import com.tg.async.mysql.pool.PoolConfiguration;
import com.tg.async.parse.IfaceParser;
import com.tg.async.parse.Parser;
import com.tg.async.utils.ResourceScanner;
import io.vertx.core.Vertx;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by twogoods on 2018/4/20.
 */
public class MapperLoader {
    private Configuration configuration;

    public void load(Configuration configuration, AsyncConfig asyncConfig, Vertx vertx) throws Exception {
        this.configuration = configuration;
        parseXmlMapper(asyncConfig.getXmlLocations());
        parseIfaceMapper(asyncConfig.getMapperPackages());
        preparePool(asyncConfig.getPoolConfiguration(), vertx);
    }

    private void parseXmlMapper(String path) throws Exception {
        Set<String> files = ResourceScanner.getXml(Arrays.asList(path.split(",")));
        for (String file : files) {
            Parser parser  = new XMLMapperBuilder(configuration, ResourceScanner.getStreamFromFile(file), file);
            parser.parse();
        }
    }

    private void parseIfaceMapper(String packageName) throws Exception {
        Set<String> classes = ResourceScanner.getClasses(Arrays.asList(packageName.split(",")));
        for (String className : classes) {
            Parser parser = new IfaceParser(configuration, className);
            parser.parse();
        }
    }

    private void preparePool(PoolConfiguration poolConfiguration, Vertx vertx) {
        if (vertx == null) {
            vertx = Vertx.vertx();
        }
        ConnectionPool pool = new ConnectionPool(poolConfiguration, vertx);
        configuration.setConnectionPool(pool);
    }
}