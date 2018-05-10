package com.tg.async.dynamic.annotation.where;

import com.tg.async.dynamic.xmltags.SqlNode;

/**
 * Created by twogoods on 2018/5/10.
 */
public interface WhereSqlGen {
    SqlNode generateWhereSql();
}
