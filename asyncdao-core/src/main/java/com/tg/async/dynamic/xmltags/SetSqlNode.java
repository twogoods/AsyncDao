package com.tg.async.dynamic.xmltags;

import java.util.Arrays;
import java.util.List;

/**
 * Created by twogoods on 2018/4/18.
 */
public class SetSqlNode extends TrimSqlNode {

    private static List<String> suffixList = Arrays.asList(",");

    public SetSqlNode(SqlNode contents) {
        super(contents, "SET", null, null, suffixList);
    }
}
