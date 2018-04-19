package com.tg.async.dynamic.xmltags;

import ognl.ClassResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/19.
 */
public class OgnlClassResolver implements ClassResolver {

    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>(101);

    @Override
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Class<?> result = null;
        if ((result = classes.get(className)) == null) {
            try {
                result = Class.forName(className);
            } catch (ClassNotFoundException e1) {
                if (className.indexOf('.') == -1) {
                    result = Class.forName("java.lang." + className);
                    classes.put("java.lang." + className, result);
                }
            }
            classes.put(className, result);
        }
        return result;
    }

}
