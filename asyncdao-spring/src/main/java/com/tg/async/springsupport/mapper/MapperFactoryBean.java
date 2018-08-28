package com.tg.async.springsupport.mapper;

import com.tg.async.mysql.AsyncDaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by twogoods on 2018/8/27.
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {
    private static final Logger log = LoggerFactory.getLogger(MapperFactoryBean.class);
    private Class<T> mapperInterface;
    private AsyncDaoFactory asyncDaoFactory;

    public MapperFactoryBean() {
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        log.debug("init {} proxy instance", mapperInterface);
        return asyncDaoFactory.getMapper(this.mapperInterface);
    }


    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }


    @Override
    public boolean isSingleton() {
        return true;
    }


    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setAsyncDaoFactory(AsyncDaoFactory asyncDaoFactory) {
        this.asyncDaoFactory = asyncDaoFactory;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

}
