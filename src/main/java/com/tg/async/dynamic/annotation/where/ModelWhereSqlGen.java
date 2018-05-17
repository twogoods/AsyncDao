package com.tg.async.dynamic.annotation.where;

import com.tg.async.annotation.ModelCondition;
import com.tg.async.annotation.ModelConditions;
import com.tg.async.constant.SqlMode;
import com.tg.async.dynamic.annotation.AbstractSectionSqlGen;
import com.tg.async.dynamic.annotation.ConditionWrap;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by twogoods on 2018/5/10.
 */
public class ModelWhereSqlGen extends AbstractSectionSqlGen {
    private ModelConditions modelConditions;
    private String paramName;

    public ModelWhereSqlGen(Method method, ModelMap modelMap, ModelConditions modelConditions, SqlMode sqlMode) {
        super(method, modelMap, sqlMode);
        this.modelConditions = modelConditions;
        paramName = method.getParameters()[0].getName();
    }

    @Override
    public SqlNode generateSql() {
        ModelCondition[] conditions = modelConditions.value();
        if (conditions.length == 0) {
            return new StaticTextSqlNode("");
        }
        List<SqlNode> nodes = new ArrayList<>();
        for (ModelCondition condition : conditions) {
            ConditionWrap conditionValue = ConditionWrap.builder()
                    .attach(condition.attach())
                    .column(condition.column())
                    .criterion(condition.criterion())
                    .field(condition.field())
                    .ognlParam(paramName + "." + condition.field())
                    .build();
            nodes.add(parse(conditionValue));
        }
        MixedSqlNode mixedSqlNode = new MixedSqlNode(nodes);
        return new WhereSqlNode(mixedSqlNode);
    }
}
