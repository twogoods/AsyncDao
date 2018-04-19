package com.tg.async.dynamic.xmltags;

import com.tg.async.exception.BuilderException;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 2018/4/19.
 */
public class OgnlCache {
    private static final Map<String, Object> expressionCache = new ConcurrentHashMap<String, Object>();

    private OgnlCache() {
        // Prevent Instantiation of Static Class
    }

    public static Object getValue(String expression, Object root) {
        try {
            Map<Object, OgnlClassResolver> context = Ognl.createDefaultContext(root, new OgnlClassResolver());
            return Ognl.getValue(parseExpression(expression), context, root);
        } catch (OgnlException e) {
            throw new BuilderException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
        }
    }

    private static Object parseExpression(String expression) throws OgnlException {
        Object node = expressionCache.get(expression);
        if (node == null) {
            node = Ognl.parseExpression(expression);
            expressionCache.put(expression, node);
        }
        return node;
    }
}
