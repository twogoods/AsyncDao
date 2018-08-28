package com.tg.async.base;

import com.tg.async.exception.MethodDefinitionException;
import com.tg.async.exception.UnsupportTypeException;
import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by twogoods on 2018/4/12.
 */
@Data
@ToString
public class MapperMethod {
    private Class iface;
    private Method method;
    private String name;
    private List<String> paramName;

    private Class wrapper;
    private Class primary;

    private boolean returnsMany = false;
    private boolean returnsMap = false;
    private boolean returnsVoid = false;
    private boolean returnsSingle = false;

    public MapperMethod(Class iface, Method method) {
        this.iface = iface;
        this.method = method;
        name = buildName(iface, method);
        parseActualType();
        parseParamName();
    }

    private void parseActualType() throws MethodDefinitionException {
        Type[] types = method.getGenericParameterTypes();
        if (types.length == 0 || !method.getParameterTypes()[types.length - 1].equals(DataHandler.class)) {
            throw new MethodDefinitionException(
                    String.format("method's param should contain DataHandler, in interface : %s, method : %s",
                            iface.getName(), method.getName()));
        }
        Type handle = types[types.length - 1];
        if (handle instanceof ParameterizedType) {
            Type handlerWrapperType = ((ParameterizedType) handle).getActualTypeArguments()[0];
            if (handlerWrapperType instanceof ParameterizedType) {
                //list or map model
                Type dataContinerType = ((ParameterizedType) handlerWrapperType).getRawType();
                if (dataContinerType.equals(List.class)) {
                    wrapper = ArrayList.class;
                    Type dataType = ((ParameterizedType) handlerWrapperType).getActualTypeArguments()[0];
                    if (dataType instanceof Class) {
                        primary = (Class) dataType;
                    } else {
                        throw new UnsupportTypeException(String.format("not support type : %s", dataContinerType));
                    }
                    returnsMany = true;
                } else if (dataContinerType.equals(Map.class)) {
                    wrapper = HashMap.class;
                    returnsMap = true;
                } else {
                    throw new UnsupportTypeException(String.format("not support type : %s", dataContinerType));
                }
            } else if (handlerWrapperType instanceof Class) {
                primary = (Class) handlerWrapperType;
                if (primary.equals(Void.class)) {
                    returnsVoid = true;
                } else {
                    returnsSingle = true;
                }
            }
        }
    }


    private void parseParamName() {
        paramName = Arrays.asList(method.getParameters()).stream().map(parameter -> parameter.getName()).collect(Collectors.toList());
    }

    private String buildName(Class iface, Method method) {
        return iface.getName() + "." + method.getName();
    }

    protected Class<?> classToCreate(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class || type == Iterable.class) {
            classToCreate = ArrayList.class;
        } else if (type == Map.class) {
            classToCreate = HashMap.class;
        } else if (type == Set.class) {
            classToCreate = HashSet.class;
        } else {
            classToCreate = type;
        }
        return classToCreate;
    }
}
