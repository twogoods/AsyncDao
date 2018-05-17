package com.tg.async.dynamic.annotation.where;

import com.tg.async.annotation.Condition;
import com.tg.async.annotation.Limit;
import com.tg.async.annotation.OffSet;
import com.tg.async.constant.SqlMode;
import com.tg.async.dynamic.annotation.AbstractSectionSqlGen;
import com.tg.async.dynamic.annotation.ConditionWrap;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.*;
import sun.jvm.hotspot.jdi.LocationImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by twogoods on 2018/5/10.
 */
public class FlatParamWhereSqlGen extends AbstractSectionSqlGen {
    public FlatParamWhereSqlGen(Method method, ModelMap modelMap, SqlMode sqlMode) {
        super(method, modelMap, sqlMode);
    }

    @Override
    public SqlNode generateSql() {
        List<SqlNode> nodes = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length - 1; i++) {
            Condition condition = parameters[i].getAnnotation(Condition.class);
            String paramName = parameters[i].getName();
            if (condition == null) {
                if (!parameters[i].isAnnotationPresent(Limit.class) && !parameters[i].isAnnotationPresent(OffSet.class)) {
                    nodes.add(new StaticTextSqlNode(simpleCondition(ConditionWrap.empty(paramName))));
                }
                continue;
            }
            ConditionWrap conditionWrap = ConditionWrap.builder()
                    .attach(condition.attach())
                    .column(condition.column())
                    .criterion(condition.criterion())
                    .field(paramName)
                    .ognlParam(paramName)
                    .build();
            nodes.add(parse(conditionWrap));
        }
        MixedSqlNode mixedSqlNode = new MixedSqlNode(nodes);
        return new WhereSqlNode(mixedSqlNode);
    }
}
