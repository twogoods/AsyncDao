package com.tg.async.dynamic.annotation.suffix;

import com.tg.async.annotation.Limit;
import com.tg.async.annotation.OffSet;
import com.tg.async.constant.SqlMode;
import com.tg.async.dynamic.annotation.AbstractSectionSqlGen;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.MixedSqlNode;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by twogoods on 2018/5/17.
 */
public class ParamSuffixGen extends AbstractSectionSqlGen {

    public ParamSuffixGen(Method method, ModelMap modelMap, SqlMode sqlMode) {
        super(method, modelMap, sqlMode);
    }

    @Override
    public SqlNode generateSql() {
        SqlNode orderNode = generateOrder();
        Optional<Parameter> limit = Arrays.asList(method.getParameters()).stream()
                .filter(parameter -> parameter.isAnnotationPresent(Limit.class))
                .findFirst();

        Optional<Parameter> offset = Arrays.asList(method.getParameters()).stream()
                .filter(parameter -> parameter.isAnnotationPresent(OffSet.class))
                .findFirst();

        StringBuilder stringBuilder = new StringBuilder();
        if (offset.isPresent() || offset.isPresent()) {
            stringBuilder.append(" limit ");
        }
        offset.ifPresent(parameter -> {
            stringBuilder.append("#{");
            stringBuilder.append(parameter.getName());
            stringBuilder.append("},");
        });

        limit.ifPresent(parameter -> {
            stringBuilder.append("#{");
            stringBuilder.append(parameter.getName());
            stringBuilder.append("}");
        });

        return new MixedSqlNode(Arrays.asList(orderNode, new StaticTextSqlNode(stringBuilder.toString())));
    }
}
