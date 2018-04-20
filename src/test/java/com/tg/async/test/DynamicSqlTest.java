package com.tg.async.test;

import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.MapperCache;
import com.tg.async.dynamic.xml.XMLMapperBuilder;
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
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(ResourceScanner.getResourceAsStream("UserTestMapper.xml"), "UserTestMapper.xml");
        xmlMapperBuilder.parse();

        MappedStatement mappedStatement = MapperCache.getMappedStatement("com.tg.test.UserMapper.queryUser");

        Map<String, Object> param = new HashMap<>();
        param.put("name", "twogoods");
        param.put("age", 12);
        param.put("ids", new int[]{1, 2, 3});

        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(param);
        System.out.println(boundSql.getSql());
        System.out.println(boundSql.getParameters());
    }

    @Test
    public void insert() throws Exception {
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(ResourceScanner.getResourceAsStream("UserTestMapper.xml"), "UserTestMapper.xml");
        xmlMapperBuilder.parse();
        MappedStatement mappedStatement = MapperCache.getMappedStatement("com.tg.test.UserMapper.insert");

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
