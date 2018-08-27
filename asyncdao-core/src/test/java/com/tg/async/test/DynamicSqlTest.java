package com.tg.async.test;

import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.mysql.Configuration;
import com.tg.async.utils.ResourceScanner;
import com.tg.async.mapper.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/19.
 */
public class DynamicSqlTest {
    @Test
    public void query() throws Exception {
        Configuration configuration = new Configuration();
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration,
                ResourceScanner.getResourceAsStream("mapper/CommonDaoMapper.xml"), "mapper/CommonDaoMapper.xml");
        xmlMapperBuilder.build();

        MappedStatement mappedStatement = configuration.getMappedStatement("com.tg.async.mapper.CommonDao.query");

        Map<String, Object> param = new HashMap<>();

        User user=new User();
        user.setUsername("twogoods");
        user.setAge(23);
        param.put("user", user);

        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(param);
        System.out.println(boundSql.getSql());
        System.out.println(boundSql.getParameters());
    }

    @Test
    public void insert() throws Exception {
        Configuration configuration = new Configuration();
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration, ResourceScanner.getResourceAsStream("mapper/CommonDaoMapper.xml"), "mapper/CommonDaoMapper.xml");
        xmlMapperBuilder.build();
        MappedStatement mappedStatement = configuration.getMappedStatement("com.tg.async.mapper.CommonDao.insert");

        User user = new User();
        user.setUsername("haha");
        user.setAge(23);
        user.setNowAddress("HZ");

        Map<String, Object> param = new HashMap<>();
        param.put("user", user);

        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(param);
        System.out.println(boundSql.getSql());
        System.out.println(boundSql.getParameters());


    }

}
