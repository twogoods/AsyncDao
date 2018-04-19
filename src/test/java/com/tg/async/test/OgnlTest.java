package com.tg.async.test;

import com.tg.async.dynamic.xmltags.OgnlCache;
import com.tg.async.mapper.User;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/19.
 */
public class OgnlTest {
    @Test
    public void base() {
        Map<String, Object> param = new HashMap<>();
        param.put("name", "haha");
        Object res = OgnlCache.getValue("name!=null and name!=''", param);
        System.out.println(res);
    }


    @Test
    public void array() {
        Map<String, Object> param = new HashMap<>();
        param.put("list", Arrays.asList(1, 2, 3));
        Object res = OgnlCache.getValue("list.size()>0", param);
        System.out.println(res);
    }

    @Test
    public void model() {
        Map<String, Object> param = new HashMap<>();
        User user = new User();
        user.setId(100L);
        user.setUsername("twogoods");
        param.put("user", user);

        Object res = OgnlCache.getValue("user.username", param);
        System.out.println(res);
    }
}
