package com.tg.async.test;

import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.mysql.Configuration;
import com.tg.async.utils.ResourceScanner;
import org.junit.Test;

/**
 * Created by twogoods on 2018/4/18.
 */
public class XmlText {

    @Test
    public void test() throws Exception {
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(new Configuration(), ResourceScanner.getResourceAsStream("mapper/CommonDaoMapper.xml"), "mapper/CommonDaoMapper.xml");
        xmlMapperBuilder.build();
    }
}
