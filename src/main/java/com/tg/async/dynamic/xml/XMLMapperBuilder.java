package com.tg.async.dynamic.xml;

import com.tg.async.dynamic.mapping.ResultMap;
import com.tg.async.dynamic.mapping.ResultMapping;
import com.tg.async.dynamic.xmltags.*;
import com.tg.async.exception.BuilderException;
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

    private final XPathParser parser;
    private final String resource;


    private final Map<String, NodeHandler> nodeHandlerMap = new HashMap<String, NodeHandler>();


    private void initNodeHandlerMap() {
        nodeHandlerMap.put("trim", new TrimHandler());
        nodeHandlerMap.put("where", new WhereHandler());
        nodeHandlerMap.put("set", new SetHandler());
        nodeHandlerMap.put("foreach", new ForEachHandler());
        nodeHandlerMap.put("if", new IfHandler());
        nodeHandlerMap.put("choose", new ChooseHandler());
        nodeHandlerMap.put("when", new IfHandler());
        nodeHandlerMap.put("otherwise", new OtherwiseHandler());
        nodeHandlerMap.put("bind", new BindHandler());
    }


    public XMLMapperBuilder(InputStream inputStream, String resource) {
        this.parser = new XPathParser(inputStream);
        this.resource = resource;
        initNodeHandlerMap();
    }


    public void parse() {
        configurationElement(parser.evalNode("/mapper"));
    }


    private void configurationElement(XNode context) {
        try {
            String namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.equals("")) {
                throw new BuilderException("Mapper's namespace cannot be empty");

            }
            //存namespace
            //builderAssistant.setCurrentNamespace(namespace);

            //TODO  parameterMap
            //parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            resultMapElements(context.evalNodes("/mapper/resultMap"));
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }


    private void resultMapElements(List<XNode> list) throws Exception {
        for (XNode resultMapNode : list) {
            try {
                resultMapElement(resultMapNode);
            } catch (Exception e) {
                // ignore, it will be retried
            }
        }
    }


    private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
        ResultMap resultMap = new ResultMap();

        String id = resultMapNode.getStringAttribute("id");
        String type = resultMapNode.getStringAttribute("type");
        resultMap.setId(id);
        resultMap.setType(type);

        List<ResultMapping> resultMappings = new ArrayList<>();
        List<XNode> resultChildren = resultMapNode.getChildren();
        for (XNode resultChild : resultChildren) {
            if ("id".equals(resultChild.getName())) {
                resultMap.setIdResultMap(buildResultMapping(resultChild));
            } else {
                resultMappings.add(buildResultMapping(resultChild));
            }
        }
        resultMap.setResultMappings(resultMappings);
        //TODO 如何存储
        return resultMap;
    }


    private ResultMapping buildResultMapping(XNode node) {
        return new ResultMapping(node.getStringAttribute("column"),
                node.getStringAttribute("property"));
    }


    private void buildStatementFromContext(List<XNode> list) {

    }


    protected MixedSqlNode parseDynamicTags(XNode node) {
        List<SqlNode> contents = new ArrayList<SqlNode>();
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

}
