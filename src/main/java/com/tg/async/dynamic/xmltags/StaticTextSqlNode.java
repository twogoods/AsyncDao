package com.tg.async.dynamic.xmltags;

/**
 * Created by twogoods on 2018/4/13.
 */
public class StaticTextSqlNode implements SqlNode {
    private final String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext dynamicContext) {
        return true;
    }

}