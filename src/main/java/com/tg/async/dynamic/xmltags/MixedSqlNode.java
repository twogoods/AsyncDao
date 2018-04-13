package com.tg.async.dynamic.xmltags;

import java.util.List;

/**
 * Created by twogoods on 2018/4/13.
 */
public class MixedSqlNode implements SqlNode {
    private final List<SqlNode> contents;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext dynamicContext) {
        return false;
    }
}
