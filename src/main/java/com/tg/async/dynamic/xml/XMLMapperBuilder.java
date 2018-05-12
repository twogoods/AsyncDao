package com.tg.async.dynamic.xml;

import com.tg.async.dynamic.mapping.*;
import com.tg.async.dynamic.xmltags.*;
import com.tg.async.exception.BuilderException;
import com.tg.async.exception.ParseException;
import com.tg.async.mysql.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by twogoods on 2018/4/12.
 */
public class XMLMapperBuilder {

    private final Configuration configuration;
    private final XPathParser parser;
    private final String resource;
    private final Map<String, NodeHandler> nodeHandlerMap = new HashMap<>();
    private String namespace;

    private void initNodeHandlerMap() {
        nodeHandlerMap.put("trim", new TrimHandler());
        nodeHandlerMap.put("where", new WhereHandler());
        nodeHandlerMap.put("set", new SetHandler());
        nodeHandlerMap.put("foreach", new ForEachHandler());
        nodeHandlerMap.put("if", new IfHandler());
    }

    public XMLMapperBuilder(Configuration configuration, InputStream inputStream, String resource) {
        this.configuration = configuration;
        this.parser = new XPathParser(inputStream);
        this.resource = resource;
        initNodeHandlerMap();
    }

    public void build() {
        configurationElement(parser.evalNode("/mapper"));
    }

    private void configurationElement(XNode context) {
        try {
            namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.equals("")) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }
            resultMapElements(context.evalNodes("/mapper/resultMap"));
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }

    private void resultMapElements(List<XNode> list) throws Exception {
        list.stream().forEach(this::resultMapElement);
    }

    private void resultMapElement(XNode resultMapNode) {
        ModelMap resultMap = new ModelMap();
        String id = resultMapNode.getStringAttribute("id");
        String type = resultMapNode.getStringAttribute("type");
        resultMap.setId(id);
        resultMap.setType(type);
        Map<String, ColumnMapping> resultMappings = new HashMap<>();
        List<XNode> resultChildren = resultMapNode.getChildren();
        for (XNode resultChild : resultChildren) {
            if ("id".equals(resultChild.getName())) {
                ColumnMapping resultMapping = buildResultMapping(resultChild);
                resultMap.setIdResultMap(resultMapping);
                resultMappings.put(resultMapping.getColumn(), resultMapping);
            } else {
                ColumnMapping resultMapping = buildResultMapping(resultChild);
                resultMappings.put(resultMapping.getColumn(), resultMapping);
            }
        }
        resultMap.setColumnKeyMappings(resultMappings);
        configuration.addModelMap(buildKey(namespace, id), resultMap);
    }

    private ColumnMapping buildResultMapping(XNode node) {
        return new ColumnMapping(node.getStringAttribute("column"),
                node.getStringAttribute("property"));
    }

    private void buildStatementFromContext(List<XNode> list) {
        list.stream().forEach(this::parseStatement);
    }

    private void parseStatement(XNode node) {
        String mode = node.getName();
        String id = node.getStringAttribute("id");
        String useGeneratedKeys = node.getStringAttribute("useGeneratedKeys");
        String keyProperty = node.getStringAttribute("keyProperty");
        String resultType = node.getStringAttribute("resultType");
        String resultMap = node.getStringAttribute("resultMap");
        if (StringUtils.isEmpty(resultMap) && StringUtils.isEmpty(resultType)) {
            throw new ParseException(id + " has no resultMap or resultType");
        }
        MixedSqlNode rootSqlNode = parseDynamicTags(node);
        MappedStatement mappedStatement = new MappedStatement.Builder(id, new DynamicSqlSource(rootSqlNode), mode)
                .keyGenerator(useGeneratedKeys)
                .keyProperty(keyProperty)
                .resultType(resultType)
                .resultMap(resultMap)
                .build();
        configuration.addMappedStatement(buildKey(namespace, id), mappedStatement);
    }


    protected MixedSqlNode parseDynamicTags(XNode node) {
        List<SqlNode> contents = new ArrayList<>();
        NodeList children = node.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            XNode child = node.newXNode(children.item(i));
            if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
                String data = child.getStringBody("");
                TextSqlNode textSqlNode = new TextSqlNode(data);
                if (textSqlNode.isDynamic()) {
                    contents.add(textSqlNode);
                } else {
                    contents.add(new StaticTextSqlNode(data));
                }
            } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNode().getNodeName();
                NodeHandler handler = nodeHandlerMap.get(nodeName);
                if (handler == null) {
                    throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
                }
                handler.handleNode(child, contents);
            }
        }
        return new MixedSqlNode(contents);
    }


    private String buildKey(String namespace, String id) {
        return namespace + "." + id;
    }

    private interface NodeHandler {
        void handleNode(XNode nodeToHandle, List<SqlNode> targetContents);
    }

    private class WhereHandler implements NodeHandler {
        public WhereHandler() {
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            WhereSqlNode where = new WhereSqlNode(mixedSqlNode);
            targetContents.add(where);
        }
    }


    private class TrimHandler implements NodeHandler {
        public TrimHandler() {
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            String prefix = nodeToHandle.getStringAttribute("prefix");
            String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
            String suffix = nodeToHandle.getStringAttribute("suffix");
            String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
            TrimSqlNode trim = new TrimSqlNode(mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
            targetContents.add(trim);
        }
    }


    private class SetHandler implements NodeHandler {
        public SetHandler() {
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            SetSqlNode set = new SetSqlNode(mixedSqlNode);
            targetContents.add(set);
        }
    }

    private class ForEachHandler implements NodeHandler {
        public ForEachHandler() {
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            String collection = nodeToHandle.getStringAttribute("collection");
            String item = nodeToHandle.getStringAttribute("item");
            String index = nodeToHandle.getStringAttribute("index");
            String open = nodeToHandle.getStringAttribute("open");
            String close = nodeToHandle.getStringAttribute("close");
            String separator = nodeToHandle.getStringAttribute("separator");
            ForEachSqlNode forEachSqlNode = new ForEachSqlNode(mixedSqlNode, collection, index, item, open, close, separator);
            targetContents.add(forEachSqlNode);
        }
    }

    private class IfHandler implements NodeHandler {
        public IfHandler() {
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
            MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            String test = nodeToHandle.getStringAttribute("test");
            IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
            targetContents.add(ifSqlNode);
        }
    }

}
