package com.tg.async.test;

import com.tg.async.annotation.Select;
import com.tg.async.annotation.Sql;
import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.mapper.CommonDao;
import com.tg.async.mysql.Configuration;
import com.tg.async.utils.ResourceScanner;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/4/18.
 */
public class XmlText {

    @Test
    public void test() throws Exception {
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(new Configuration(), ResourceScanner.getResourceAsStream("CommonDaoMapper.xml"), "CommonDaoMapper.xml");
        xmlMapperBuilder.build();
    }
}
