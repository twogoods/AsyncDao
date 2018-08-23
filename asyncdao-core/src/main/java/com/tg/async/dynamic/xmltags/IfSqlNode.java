package com.tg.async.dynamic.xmltags;

/**
 * Created by twogoods on 2018/4/13.
 */
public class IfSqlNode implements SqlNode {

    private final String test;
    private final SqlNode contents;

    public IfSqlNode(SqlNode contents, String test) {
        this.test = test;
        this.contents = contents;
    }


    @Override
    public boolean apply(DynamicContext dynamicContext) {
        if (ExpressionEvaluator.evaluateBoolean(test, dynamicContext.getParam())) {
            contents.apply(dynamicContext);
            return true;
        }
        return false;
    }
}
