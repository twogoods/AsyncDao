package com.tg.async.dynamic.annotation.suffix;

import com.tg.async.annotation.Page;
import com.tg.async.constant.SqlMode;
import com.tg.async.dynamic.annotation.AbstractSectionSqlGen;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.xmltags.IfSqlNode;
import com.tg.async.dynamic.xmltags.MixedSqlNode;
import com.tg.async.dynamic.xmltags.SqlNode;
import com.tg.async.dynamic.xmltags.StaticTextSqlNode;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by twogoods on 2018/5/17.
 */
public class ModelSuffixGen extends AbstractSectionSqlGen {
    private String paramName;
    private final String offset = " #{ %s.%s }, ";
    private final String limit = " #{ %s.%s } ";
    private final String ifCondition = " %s.%s != null ";

    public ModelSuffixGen(Method method, ModelMap modelMap, SqlMode sqlMode) {
        super(method, modelMap, sqlMode);
        paramName = method.getParameters()[0].getName();
    }

    @Override
    public SqlNode generateSql() {
        SqlNode orderNode = generateOrder();

        Page page = method.getAnnotation(Page.class);
        if (page == null) {
            return orderNode;
        }

        SqlNode offsetNode = new IfSqlNode(new StaticTextSqlNode(String.format(offset, paramName, page.offsetField())),
                String.format(ifCondition, paramName, page.offsetField()));
        return new MixedSqlNode(Arrays.asList(
                orderNode,
                new StaticTextSqlNode("limit"),
                offsetNode,
                new StaticTextSqlNode(String.format(limit, paramName, page.limitField()))
        ));
    }
}
