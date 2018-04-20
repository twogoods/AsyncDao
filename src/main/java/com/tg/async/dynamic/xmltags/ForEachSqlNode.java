package com.tg.async.dynamic.xmltags;

import com.tg.async.dynamic.xml.GenericTokenParser;

import java.util.Map;

/**
 * Created by twogoods on 2018/4/13.
 */
public class ForEachSqlNode implements SqlNode {
    public static final String ITEM_PREFIX = "__frch_";

    private final String collectionExpression;
    private final SqlNode contents;
    private final String open;
    private final String close;
    private final String separator;
    private final String item;
    private final String index;

    public ForEachSqlNode(SqlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
        this.collectionExpression = collectionExpression;
        this.contents = contents;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.index = index;
        this.item = item;
    }


    @Override
    public boolean apply(DynamicContext dynamicContext) {
        Object bindings = dynamicContext.getParam();
        final Iterable<?> iterable = ExpressionEvaluator.evaluateIterable(collectionExpression, bindings);
        if (!iterable.iterator().hasNext()) {
            return true;
        }
        applyOpen(dynamicContext);
        boolean first = true;
        int i = 0;
        for (Object o : iterable) {
            DynamicContext oldContext = dynamicContext;
            if (first || separator == null) {
                dynamicContext = new PrefixedContext(dynamicContext, "");
            } else {
                dynamicContext = new PrefixedContext(dynamicContext, separator);
            }
            applyItem(dynamicContext, o, i);
            contents.apply(new FilteredDynamicContext(dynamicContext, index, item, i));
            if (first) {
                first = !((PrefixedContext) dynamicContext).isPrefixApplied();
            }
            dynamicContext = oldContext;
            i++;
        }
        applyClose(dynamicContext);
        return true;
    }

    private void applyItem(DynamicContext context, Object o, int i) {
        if (item != null) {
            context.bind(itemizeItem(item, i), o);
        }
    }

    private static String itemizeItem(String item, int i) {
        return new StringBuilder(ITEM_PREFIX).append(item).append("_").append(i).toString();
    }

    private void applyOpen(DynamicContext context) {
        if (open != null) {
            context.appendSql(open);
        }
    }

    private void applyClose(DynamicContext context) {
        if (close != null) {
            context.appendSql(close);
        }
    }

    private static class FilteredDynamicContext extends DynamicContext {
        private final DynamicContext delegate;
        private final int index;
        private final String itemIndex;
        private final String item;

        public FilteredDynamicContext(DynamicContext delegate, String itemIndex, String item, int i) {
            this.delegate = delegate;
            this.index = i;
            this.itemIndex = itemIndex;
            this.item = item;
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public void appendSql(String sql) {
            GenericTokenParser parser = new GenericTokenParser("#{", "}", new TokenHandler() {
                @Override
                public String handleToken(String content) {
                    String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
                    if (itemIndex != null && newContent.equals(content)) {
                        newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
                    }
                    return new StringBuilder("#{").append(newContent).append("}").toString();
                }
            });
            delegate.appendSql(parser.parse(sql));
        }

        @Override
        public void bind(String key, Object value) {
            delegate.bind(key, value);
        }

        @Override
        public Map<String, Object> getBindParam() {
            return delegate.getBindParam();
        }

        @Override
        public Object getParam() {
            return delegate.getParam();
        }
    }


    private class PrefixedContext extends DynamicContext {
        private final DynamicContext delegate;
        private final String prefix;
        private boolean prefixApplied;

        public PrefixedContext(DynamicContext delegate, String prefix) {
            this.delegate = delegate;
            this.prefix = prefix;
            this.prefixApplied = false;
        }

        public boolean isPrefixApplied() {
            return prefixApplied;
        }


        @Override
        public void bind(String key, Object value) {
            delegate.bind(key, value);
        }

        @Override
        public Map<String, Object> getBindParam() {
            return delegate.getBindParam();
        }

        @Override
        public Object getParam() {
            return delegate.getParam();
        }

        @Override
        public void appendSql(String sql) {
            if (!prefixApplied && sql != null && sql.trim().length() > 0) {
                delegate.appendSql(prefix);
                prefixApplied = true;
            }
            delegate.appendSql(sql);
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

    }


}
