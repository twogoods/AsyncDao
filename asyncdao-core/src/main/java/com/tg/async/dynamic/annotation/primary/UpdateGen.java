package com.tg.async.dynamic.annotation.primary;

import com.tg.async.annotation.ModelConditions;
import com.tg.async.annotation.Update;
import com.tg.async.dynamic.annotation.AbstractSqlGen;
import com.tg.async.dynamic.annotation.where.ModelWhereSqlGen;
import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.*;
import com.tg.async.exception.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by twogoods on 2018/5/11.
 */
public class UpdateGen extends AbstractSqlGen {
    private Update update;

    public UpdateGen(Method method, ModelMap modelMap, Update update) {
        super(method, modelMap);
        this.update = update;
        ModelConditions modelConditions = method.getAnnotation(ModelConditions.class);
        if (modelConditions == null) {
            throw new ParseException("update sql should annotated @ModelConditions");
        }
        this.abstractWhereSqlGen = new ModelWhereSqlGen(method, modelMap, modelConditions, update.sqlMode());
    }

    @Override
    protected SqlNode generateBaseSql() {
        String paramName = method.getParameters()[0].getName();
        List<SqlNode> updateNode = new ArrayList<>();
        if (StringUtils.isEmpty(update.columns())) {
            for (Map.Entry<String, ColumnMapping> entry : modelMap.getFieldKeyMappings().entrySet()) {
                if (entry.getKey().equals(modelMap.getIdResultMap().getProperty())) {
                    continue;
                }
                String test = String.format(testTemplate, paramName, entry.getKey());
                updateNode.add(new IfSqlNode(new StaticTextSqlNode(setStr(entry.getValue().getColumn(), paramName)), test));
            }
        } else {
            String[] columns = update.columns().split(",");
            for (String column : columns) {
                String field = getFieldByColumn(column);
                String test = String.format(testTemplate, paramName, field);
                updateNode.add(new IfSqlNode(new StaticTextSqlNode(setStr(column, paramName)), test));
            }
        }
        return new MixedSqlNode(Arrays.asList(
                new StaticTextSqlNode("update " + modelMap.getTable()),
                new SetSqlNode(new MixedSqlNode(updateNode))
        ));
    }


    private String getFieldByColumn(String column) {
        ColumnMapping columnMapping = modelMap.getColumnKeyMappings().get(column);
        return columnMapping == null ? column : columnMapping.getProperty();
    }


    private String setStr(String column, String paramName) {
        return column + "= #{" + paramName + "." + column + "},";
    }

    @Override
    public String sqlType() {
        return "update";
    }
}
