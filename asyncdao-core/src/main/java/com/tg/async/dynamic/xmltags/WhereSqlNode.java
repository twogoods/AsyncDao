package com.tg.async.dynamic.xmltags;

import java.util.Arrays;
import java.util.List;

/**
 * Created by twogoods on 2018/4/13.
 */
public class WhereSqlNode extends TrimSqlNode {


    private static List<String> prefixList = Arrays.asList("AND ", "OR ", "AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

    public WhereSqlNode(SqlNode contents) {
        super(contents, "WHERE", prefixList, null, null);
    }
}
