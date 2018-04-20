package com.tg.async.dynamic.mapping;

import com.tg.async.dynamic.xml.GenericTokenParser;
import com.tg.async.dynamic.xmltags.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/13.
 */
public class DynamicSqlSource implements SqlSource {

    private final SqlNode rootSqlNode;

    public DynamicSqlSource(SqlNode rootSqlNode) {
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        DynamicContext dynamicContext = new DynamicContext(parameterObject);
        rootSqlNode.apply(dynamicContext);
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(parameterObject, dynamicContext.getBindParam());
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(dynamicContext.getSql());
        return new BoundSql(sql, handler.getRealParameters());
    }

    private static class ParameterMappingTokenHandler implements TokenHandler {
        private Object param;
        private Map<String, Object> additionalParameters;
        private List<Object> realParameters = new ArrayList<>();

        public ParameterMappingTokenHandler(Object param, Map<String, Object> additionalParameters) {
            this.param = param;
            this.additionalParameters = additionalParameters;
        }

        @Override
        public String handleToken(String content) {
            if (content.startsWith(ForEachSqlNode.ITEM_PREFIX)) {
                realParameters.add(OgnlCache.getValue(content, additionalParameters));
            } else {
                realParameters.add(OgnlCache.getValue(content, param));
            }
            return "?";
        }

        public List<Object> getRealParameters() {
            return realParameters;
        }
    }

}
