package com.tg.async.mysql;

import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.mysql.pool.ConnectionPool;
import com.tg.async.mysql.pool.PoolConfiguration;
import com.tg.async.parse.IfaceParser;
import com.tg.async.utils.ResourceScanner;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by twogoods on 2018/4/20.
 */
public class MapperLoader {
    private static final Logger log = LoggerFactory.getLogger(MapperLoader.class);
    private Configuration configuration;

    public void load(Configuration configuration, AsyncConfig asyncConfig, Vertx vertx) throws Exception {
        this.configuration = configuration;
        parseXmlMapper(asyncConfig.getXmlLocations());
        parseIfaceMapper(asyncConfig.getMapperPackages());
        preparePool(asyncConfig.getPoolConfiguration(), vertx);
    }

    private void parseXmlMapper(List<String> path) throws Exception {
        if (path==null||path.size()==0) {
            log.warn("XmlLocations is empty, check config");
            return;
        }
        Set<String> files = ResourceScanner.getXml(path);
        for (String file : files) {
            XMLMapperBuilder builder = new XMLMapperBuilder(configuration, ResourceScanner.getStreamFromFile(file), file);
            builder.build();
        }
    }

    private void parseIfaceMapper(List<String> packageName) throws Exception {
        if (packageName==null||packageName.size()==0) {
            log.warn("package is empty, check config");
            return;
        }
        Set<String> classes = ResourceScanner.getClasses(packageName);
        IfaceParser ifaceParser = new IfaceParser(configuration);
        for (String className : classes) {
            ifaceParser.parse(className);
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