package com.tg.async.dynamic.annotation.primary;

import com.tg.async.annotation.Insert;
import com.tg.async.dynamic.annotation.AbstractSqlGen;
import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.dynamic.mapping.DynamicSqlSource;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by twogoods on 2018/5/10.
 */
public class InsertGen extends AbstractSqlGen {
    private Insert insert;

    public InsertGen(Method method, Insert insert, ModelMap modelMap) {
        super(method, modelMap);
        this.insert = insert;
    }



    @Override
    protected SqlNode generateBaseSql() {
        String data = String.format("insert into %s ", modelMap.getTable());
        String paramName = method.getParameters()[0].getName();

        List<SqlNode> columnNode = new ArrayList<>();
        List<SqlNode> valueNode = new ArrayList<>();
        for (Map.Entry<String, ColumnMapping> entry : modelMap.getFieldKeyMappings().entrySet()) {
            if (entry.getKey().equals(modelMap.getIdResultMap().getProperty())) {
                continue;
            }
            String test = String.format(testTemplate, paramName, entry.getKey());
            columnNode.add(new IfSqlNode(new StaticTextSqlNode(entry.getValue().getColumn() + ","), test));
            valueNode.add(new IfSqlNode(new StaticTextSqlNode("#{" + paramName + "." + entry.getKey() + "},"), test));
        }

        List<SqlNode> mixedSqlNode = Arrays.asList(
                new StaticTextSqlNode(data),
                new TrimSqlNode(new MixedSqlNode(columnNode), "(", null, ")", ","),
                new TrimSqlNode(new MixedSqlNode(valueNode), "values (", null, ")", ",")
        );
        return new MixedSqlNode(mixedSqlNode);
    }

    @Override
    public MappedStatement generate() {
        SqlNode rootSqlNode = generateBaseSql();
        MappedStatement mappedStatement = new MappedStatement.Builder(buildKey(), new DynamicSqlSource(rootSqlNode), sqlType())
                .keyGenerator(String.valueOf(insert.useGeneratedKeys()))
                .keyProperty(insert.keyProperty())
                .parameterType(null)
                .resultType(null)
                .resultMap(null)
                .build();
        return mappedStatement;
    }

    @Override
    public String sqlType() {
        return "insert";
    }
}
