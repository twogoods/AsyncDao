package com.tg.async.test;

import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.MapperCache;
import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.io.Resources;
import org.junit.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/19.
 */
public class DynamicSqlTest {

    @Test
    public void test() throws Exception {
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream("UserTestMapper.xml"), "UserTestMapper.xml");
        xmlMapperBuilder.parse();

        MappedStatement mappedStatement = MapperCache.getMappedStatement("com.tg.test.UserMapper.queryUser");

        Map<String, Object> param = new HashMap<>();
        param.put("name", "haha");
        param.put("age", 12);
        param.put("ids", Arrays.asList(1, 2, 3));

        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(param);
        System.out.println(boundSql);


    }
}
