package com.tg.async.utils;

import com.github.mauricio.async.db.QueryResult;
import com.github.mauricio.async.db.ResultSet;
import com.github.mauricio.async.db.RowData;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.mapping.ColumnMapping;
import com.tg.async.mysql.ScalaUtils;
import lombok.extern.slf4j.Slf4j;
import scala.Option;
import scala.collection.Iterator;
import scala.runtime.AbstractFunction1;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 2018/5/2.
 */
@Slf4j
public class DataConverter {
    private static Map<Class, Map<String, PropertyDescriptor>> classesWithProperty = new ConcurrentHashMap<>();

    public static <T> List<T> queryResultToListObject(QueryResult queryResult, Class<T> clazz, ModelMap resultMap) {
        final Option<ResultSet> rows = queryResult.rows();
        java.util.List<T> list = new ArrayList<T>();
        if (rows.isDefined()) {
            List<String> columnNames = ScalaUtils.toJavaList(rows.get().columnNames().toList());
            rows.get().foreach(new AbstractFunction1<RowData, Void>() {
                @Override
                public Void apply(RowData row) {
                    try {
                        list.add(rowDataToObject(row, clazz, resultMap, columnNames));
                    } catch (Exception e) {
                        log.error("convert object error :{}", e);
                    }
                    return null;
                }
            });
        }
        return list;
    }

    public static <T> T queryResultToObject(QueryResult queryResult, Class<T> clazz, ModelMap resultMap) {
        final Option<ResultSet> rows = queryResult.rows();
        if (rows.isDefined()) {
            List<String> columnNames = ScalaUtils.toJavaList(rows.get().columnNames().toList());
            Iterator<RowData> iterator = rows.get().iterator();
            if (iterator.hasNext()) {
                try {
                    return rowDataToObject(iterator.next(), clazz, resultMap, columnNames);
                } catch (Exception e) {
                    log.error("convert object error :{}", e);
                }
            }
        }
        return null;
    }

    public static Map<String, Object> queryResultToMap(QueryResult queryResult, ModelMap resultMap) {
        final Option<ResultSet> rows = queryResult.rows();
        Map<String, Object> map = new HashMap<>();
        if (rows.isDefined()) {
            List<String> columnNames = ScalaUtils.toJavaList(rows.get().columnNames().toList());
            Iterator<RowData> iterator = rows.get().iterator();
            if (iterator.hasNext()) {
                rowDataToMap(iterator.next(), resultMap, columnNames);
            }
        }
        return map;
    }

    public static Map<String, Object> rowDataToMap(RowData rowData, ModelMap resultMap, List<String> columnNames) {
        Map<String, Object> res = new HashMap<>();
        Iterator<Object> iterable = rowData.iterator();
        int index = 0;
        while (iterable.hasNext()) {
            Object item = iterable.next();
            String property = getProperty(resultMap, columnNames.get(index));
            res.put(property, item);
            index++;
        }
        return res;
    }


    public static <T> T rowDataToObject(RowData rowData, Class<T> clazz, ModelMap resultMap, List<String> columnNames) throws Exception {
        T t = clazz.newInstance();
        Iterator<Object> iterable = rowData.iterator();
        int index = 0;
        while (iterable.hasNext()) {
            Object item = iterable.next();
            String property = getProperty(resultMap, columnNames.get(index));
            setProperty(clazz, t, property, item);
            index++;
        }
        return t;
    }


    private static void setProperty(Class clazz, Object object, String property, Object value) {
        Map<String, PropertyDescriptor> properties = classesWithProperty.get(clazz);
        if (properties == null) {
            synchronized (clazz) {
                if ((properties = classesWithProperty.get(clazz)) == null) {
                    properties = initpropertyDescriptors(clazz);
                    classesWithProperty.put(clazz, properties);
                }
            }
        }
        try {
            PropertyDescriptor propertyDescriptor = properties.get(property);
            if (propertyDescriptor == null) {
                log.error("can't find property: {}", property);
                return;
            }
            propertyDescriptor.setValue(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Map<String, PropertyDescriptor> initpropertyDescriptors(Class clazz) {
        Map<String, PropertyDescriptor> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            map.put(field.getName(), new PropertyDescriptor(clazz, field.getName()));
        }
        return map;
    }

    private static String getProperty(ModelMap resultMap, String columnName) {
        if (resultMap == null) {
            return columnName;
        }
        ColumnMapping resultMapping = resultMap.getColumnKeyMappings().get(columnName);
        if (resultMapping == null) {
            return columnName;
        }
        return resultMapping.getProperty();
    }
}
