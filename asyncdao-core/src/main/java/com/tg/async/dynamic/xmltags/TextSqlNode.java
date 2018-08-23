package com.tg.async.dynamic.xmltags;


import com.tg.async.dynamic.xml.GenericTokenParser;

/**
 * Created by twogoods on 2018/4/13.
 */
public class TextSqlNode implements SqlNode {
    private final String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    public boolean isDynamic() {
        DynamicCheckerTokenParser checker = new DynamicCheckerTokenParser();
        GenericTokenParser parser = createParser(checker);
        parser.parse(text);
        return checker.isDynamic();
    }

    @Override
    public boolean apply(DynamicContext dynamicContext) {
        GenericTokenParser parser = createParser(new BindingTokenParser(dynamicContext));
        dynamicContext.appendSql(parser.parse(text));
        return true;
    }

    private GenericTokenParser createParser(TokenHandler handler) {
        return new GenericTokenParser("${", "}", handler);
    }

    private static class BindingTokenParser implements TokenHandler {
        private DynamicContext context;

        public BindingTokenParser(DynamicContext context) {
            this.context = context;
        }

        @Override
        public String handleToken(String content) {
            Object parameter = context.getParam();
            if (parameter == null) {
                return "";
            }
            Object value = OgnlCache.getValue(content, parameter);
            String srtValue = (value == null ? "" : String.valueOf(value));
            return srtValue;
        }
    }

    private static class DynamicCheckerTokenParser implements TokenHandler {
        private boolean isDynamic;

        public DynamicCheckerTokenParser() {
        }

        public boolean isDynamic() {
            return isDynamic;
        }

        @Override
        public String handleToken(String content) {
            this.isDynamic = true;
            return null;
        }
    }
}
