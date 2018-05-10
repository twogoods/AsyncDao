package com.tg.async.dynamic.annotation.where;

import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.ForEachSqlNode;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2018/5/10.
 */
public abstract class AbstractWhereSqlGen implements WhereSqlGen {
    protected Method method;
    protected ModelMap modelMap;

    public AbstractWhereSqlGen(Method method, ModelMap modelMap) {
        this.method = method;
        this.modelMap = modelMap;
    }

    @Override
    public SqlNode generateWhereSql() {
        return null;
    }

    protected String getColumn(String field, String column) {
        if (StringUtils.isNotEmpty(column)) {
            return column;
        }

        ColumnMapping columnMapping = modelMap.getFieldKeyMappings().get(field);
        return columnMapping == null ? field : columnMapping.getColumn();
    }


    protected SqlNode generateForEachNode(String collectionExpression) {
        return new ForEachSqlNode(new StaticTextSqlNode("#{item}"), collectionExpression, null, "item", "(", ")", ",");

    }

}
