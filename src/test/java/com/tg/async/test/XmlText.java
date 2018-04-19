package com.tg.async.test;

import com.tg.async.dynamic.xml.XMLMapperBuilder;
import com.tg.async.dynamic.xml.XPathParser;
import com.tg.async.io.Resources;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by twogoods on 2018/4/18.
 */
public class XmlText {

    @Test
    public void test() throws Exception {
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream("UserTestMapper.xml"), "UserTestMapper.xml");
        xmlMapperBuilder.parse();
    }
}
