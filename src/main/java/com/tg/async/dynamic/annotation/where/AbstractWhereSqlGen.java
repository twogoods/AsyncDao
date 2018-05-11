package com.tg.async.dynamic.annotation.where;

import com.tg.async.constant.SqlMode;
import com.tg.async.dynamic.annotation.ConditionWrap;
import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by twogoods on 2018/5/10.
 */
public abstract class AbstractWhereSqlGen implements WhereSqlGen {
    protected Method method;
    protected ModelMap modelMap;
    protected SqlMode sqlMode;

    public AbstractWhereSqlGen(Method method, ModelMap modelMap, SqlMode sqlMode) {
        this.method = method;
        this.modelMap = modelMap;
        this.sqlMode = sqlMode;
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

    protected SqlNode parse(ConditionWrap condition) {
        if (sqlMode.equals(SqlMode.COMMON)) {
            if (!condition.getCriterion().inCriterion()) {
                return new StaticTextSqlNode(simpleCondition(condition));
            } else {
                String sql = condition.getAttach().toString() + " " + getColumn(condition.getField(), condition.getColumn()) + " " + condition.getCriterion().getCriterion();
                return new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(sql), generateForEachNode(condition.getOgnlParam())));
            }
        } else {
            if (!condition.getCriterion().inCriterion()) {
                return new IfSqlNode(new StaticTextSqlNode(simpleCondition(condition)), getTestStr(condition));
            } else {
                String sql = condition.getAttach().toString() + " " + getColumn(condition.getField(), condition.getColumn()) + " " + condition.getCriterion().getCriterion();
                SqlNode mixedSqlNode = new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(sql), generateForEachNode(condition.getOgnlParam())));
                return new IfSqlNode(mixedSqlNode, getTestStr(condition));
            }
        }
    }

    private String getTestStr(ConditionWrap conditionWrap) {
        if (StringUtils.isEmpty(conditionWrap.getTest())) {
            return conditionWrap.getOgnlParam() + "!=null";
        }
        return conditionWrap.getTest();
    }

    protected String simpleCondition(ConditionWrap condition) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        stringBuilder.append(condition.getAttach().toString())
                .append(" ")
                .append(getColumn(condition.getField(), condition.getColumn()))
                .append(" ")
                .append(condition.getCriterion().getCriterion())
                .append(" #{")
                .append(condition.getOgnlParam())
                .append("}");
        return stringBuilder.toString();
    }
}
