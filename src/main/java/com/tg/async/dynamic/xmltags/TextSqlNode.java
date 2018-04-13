package com.tg.async.dynamic.xmltags;


/**
 * Created by twogoods on 2018/4/13.
 */
public class TextSqlNode implements SqlNode {
    private final String text;

    public TextSqlNode(String text) {
        this.text = text;
    }


    public boolean isDynamic() {

        //TODO 判断是否是动态语句
        return false;
    }

    @Override
    public boolean apply(DynamicContext dynamicContext) {


        return true;
    }


}
