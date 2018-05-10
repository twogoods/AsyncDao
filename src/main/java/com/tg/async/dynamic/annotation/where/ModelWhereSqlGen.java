package com.tg.async.dynamic.annotation.where;

import com.tg.async.annotation.ModelCondition;
import com.tg.async.annotation.ModelConditions;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by twogoods on 2018/5/10.
 */
public class ModelWhereSqlGen extends AbstractWhereSqlGen {

    private ModelConditions modelConditions;

    private String paramName;

    public ModelWhereSqlGen(Method method, ModelMap modelMap, ModelConditions modelConditions) {
        super(method, modelMap);
        this.modelConditions = modelConditions;
        paramName = method.getParameters()[0].getName();
    }

    @Override
    public SqlNode generateWhereSql() {
        ModelCondition[] conditions = modelConditions.value();
        if (conditions.length == 0) {
            return new StaticTextSqlNode("");
        }
        List<SqlNode> nodes = new ArrayList<>();
        for (ModelCondition condition : conditions) {
            if (StringUtils.isEmpty(condition.test())) {
                if (!condition.criterion().inCriterion()) {
                    nodes.add(new StaticTextSqlNode(simpleCondition(condition)));
                } else {
                    String sql = condition.attach().toString() + " " + getColumn(condition.field(), condition.column()) + " " + condition.criterion().getCriterion();
                    nodes.add(new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(sql), generateForEachNode(paramName + "." + condition.field()))));
                }
            } else {
                if (!condition.criterion().inCriterion()) {
                    nodes.add(new IfSqlNode(new StaticTextSqlNode(simpleCondition(condition)), condition.test()));
                } else {
                    String sql = condition.attach().toString() + " " + getColumn(condition.field(), condition.column()) + " " + condition.criterion().getCriterion();
                    SqlNode mixedSqlNode = new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(sql), generateForEachNode(paramName + "." + condition.field())));
                    nodes.add(new IfSqlNode(mixedSqlNode, condition.test()));
                }
            }
        }
        MixedSqlNode mixedSqlNode = new MixedSqlNode(nodes);
        return new WhereSqlNode(mixedSqlNode);
    }


    private String simpleCondition(ModelCondition condition) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        stringBuilder.append(condition.attach().toString())
                .append(" ")
                .append(getColumn(condition.field(), condition.column()))
                .append(" ")
                .append(condition.criterion().getCriterion())
                .append(" #{")
                .append(paramName)
                .append(".")
                .append(condition.field())
                .append("}");
        return stringBuilder.toString();
    }
}
