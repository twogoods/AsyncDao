package com.tg.async.parse;

import com.tg.async.annotation.*;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.exception.BuilderException;
import com.tg.async.mysql.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by twogoods on 2018/4/27.
 */
public class IfaceParser {
    private Configuration configuration;

    public IfaceParser(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(String className) {
        try {
            Class clazz = Class.forName(className);
            if (!clazz.isInterface()) {
                return;
            }

            Annotation annotation = clazz.getAnnotation(Sql.class);
            if (annotation != null) {
                parseModelMapping(((Sql) annotation).value(), className);
            }

            Method[] methods = clazz.getMethods();
            AnnotatedSQLParser annotatedSQLParser = new AnnotatedSQLParser(configuration, className);
            for (Method method : methods) {
                configuration.addMapperMethod(method, new MapperMethod(clazz, method));
                annotatedSQLParser.parse(method);
            }
        } catch (ClassNotFoundException e) {
            throw new BuilderException(e);
        }
    }


    private void parseModelMapping(Class clazz, String ifaceName) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return;
        }
        Table table = (Table) clazz.getAnnotation(Table.class);
        ModelMap modelMap = new ModelMap();
        modelMap.setTable(table.name());
        modelMap.setClazz(clazz);


        Field[] fields = clazz.getDeclaredFields();

        List<ColumnMapping> idColumns = Arrays.asList(fields).stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(this::generateColumnMapping).collect(Collectors.toList());

        modelMap.setIdResultMap(idColumns.size() == 0 ? null : idColumns.get(0));

        Map<String, ColumnMapping> columnMappings = Arrays.asList(fields).stream()
                .filter(field -> field.isAnnotationPresent(Ignore.class))
                .map(this::generateColumnMapping)
                .collect(Collectors.toMap(ColumnMapping::getColumn, columnMapping -> columnMapping));

        modelMap.setResultMappings(columnMappings);

        configuration.addModelMap(ifaceName, modelMap);
    }


    private ColumnMapping generateColumnMapping(Field field) {
        ColumnMapping columnMapping = new ColumnMapping();
        if (field.isAnnotationPresent(Id.class)) {
            String id = field.getAnnotation(Id.class).value();
            return generateColumnMapping(id, field.getName());
        }

        if (field.isAnnotationPresent(Column.class)) {
            String column = field.getAnnotation(Column.class).value();
            return generateColumnMapping(column, field.getName());
        }
        return generateColumnMapping(null, field.getName());

    }

    private ColumnMapping generateColumnMapping(String column, String property) {
        ColumnMapping columnMapping = new ColumnMapping();
        if (StringUtils.isEmpty(column)) {
            columnMapping.setColumn(column);
        } else {
            columnMapping.setColumn(property);
        }
        columnMapping.setProperty(property);
        return columnMapping;

    }

}
