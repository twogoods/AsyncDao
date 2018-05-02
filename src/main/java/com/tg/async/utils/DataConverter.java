package com.tg.async.utils;

import com.github.mauricio.async.db.QueryResult;
import com.github.mauricio.async.db.ResultSet;
import com.github.mauricio.async.db.RowData;
import com.tg.async.dynamic.mapping.ResultMap;
import com.tg.async.mysql.ScalaUtils;
import io.vertx.core.json.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import scala.Option;
import scala.collection.Iterator;
import scala.runtime.AbstractFunction1;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 2018/5/2.
 */
@Slf4j
public class DataConverter {


    private static Map<Class, Map<String, PropertyDescriptor>> classesWithProperty = new ConcurrentHashMap<>();


    public static <T> List<T> queryResultToListObject(QueryResult queryResult, Class<T> clazz, ResultMap resultMap) throws IllegalAccessException, InstantiationException {
        final Option<ResultSet> rows = queryResult.rows();
        java.util.List<T> list = new ArrayList<T>();
        if (!rows.isDefined()) {
            //empty
        } else {
            List<String> columnNames = ScalaUtils.toJavaList(rows.get().columnNames().toList());
            rows.get().foreach(new AbstractFunction1<RowData, Void>() {
                @Override
                public Void apply(RowData row) {
                    try {
                        rowDataToObject(row, clazz, resultMap, columnNames);
                    } catch (Exception e) {
                        log.error("convert object error :{}", e);
                    }
                    return null;
                }
            });

        }
        return list;
    }

    public static <T> T queryResultToObject(QueryResult queryResult, Class<T> clazz, ResultMap resultMap) throws Exception {
        final Option<ResultSet> rows = queryResult.rows();
        if (!rows.isDefined()) {
            //empty
        } else {
            List<String> columnNames = ScalaUtils.toJavaList(rows.get().columnNames().toList());
            Iterator<RowData> iterator = rows.get().iterator();
            if (iterator.hasNext()) {
                return rowDataToObject(iterator.next(), clazz, resultMap, columnNames);
            }
        }
        return null;
    }

    public static Map<String, Object> queryResultToMap(QueryResult queryResult, ResultMap resultMap) {
        final Option<ResultSet> rows = queryResult.rows();
        Map<String, Object> map = new HashMap<>();
        if (!rows.isDefined()) {
            //empty
        } else {
            final List<String> names = ScalaUtils.toJavaList(rows.get().columnNames().toList());
            rows.get().foreach(new AbstractFunction1<RowData, Void>() {
                @Override
                public Void apply(RowData row) {
                    try {
                        //rowDataToObject(row, clazz, resultMap);
                    } catch (Exception e) {
                        log.error("convert object error :{}", e);
                    }
                    return null;
                }
            });

        }
        return map;
    }

    public static <T> T rowDataToObject(RowData rowData, Class<T> clazz, ResultMap resultMap, List<String> columnNames) throws Exception {
        T t = clazz.newInstance();

        Iterator<Object> iterable = rowData.iterator();
        int index = 0;
        while (iterable.hasNext()) {
            Object item = iterable.next();
            String property = getProperty(resultMap, columnNames.get(index));

        }


        rowData.foreach(new AbstractFunction1<Object, Void>() {
            @Override
            public Void apply(Object value) {
                return null;
            }
        });
        return t;
    }


    private static void setProperty(Class clazz, Object object, String property, Object value) {
        Map<String, PropertyDescriptor> properties = classesWithProperty.get(clazz);
        if (properties == null) {
            synchronized (clazz) {
                if ((properties = classesWithProperty.get(clazz)) == null) {
                    classesWithProperty.put(clazz, initpropertyDescriptors(clazz));
                }

            }
        }
        try {
            PropertyDescriptor propertyDescriptor = properties.get(property);
            if (propertyDescriptor == null) {
                log.error("can't find property: {}", property);
                return;
            }
            propertyDescriptor.setvalue(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private static Map<String, PropertyDescriptor> initpropertyDescriptors(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();


        return null;
    }

    private static String getProperty(ResultMap resultMap, String columnName) {
        if (resultMap == null) {
            return columnName;
        }
        return resultMap.getResultMappingItem(columnName).getProperty();
    }


    private static void convertValue(JsonArray array, Object value) {
        if (value == null) {
            array.addNull();
        } else if (value instanceof scala.math.BigDecimal) {
            array.add(value.toString());
        } else if (value instanceof LocalDateTime) {
            array.add(value.toString());
        } else if (value instanceof LocalDate) {
            array.add(value.toString());
        } else if (value instanceof DateTime) {
            array.add(Instant.ofEpochMilli(((DateTime) value).getMillis()));
        } else if (value instanceof UUID) {
            array.add(value.toString());
        } else if (value instanceof scala.collection.mutable.ArrayBuffer) {
            scala.collection.mutable.ArrayBuffer<Object> arrayBuffer = (scala.collection.mutable.ArrayBuffer<Object>) value;
            JsonArray subArray = new JsonArray();
            arrayBuffer.foreach(new AbstractFunction1<Object, Void>() {

                @Override
                public Void apply(Object subValue) {
                    convertValue(subArray, subValue);
                    return null;
                }

            });
            array.add(subArray);
        } else {
            array.add(value);
        }
    }
}
