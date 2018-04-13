package com.tg.async.base;

import com.tg.async.exception.MethodDefinitionException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by twogoods on 2018/4/12.
 */
@Data
@ToString
@Slf4j
public class MapperMethod {

    private Class iface;
    private Method method;
    private Type actualType;


    public MapperMethod(Class iface, Method method) {
        this.iface = iface;
        this.method = method;
        parseActualType();
    }

    private void parseActualType() throws MethodDefinitionException {
        Type[] types = method.getGenericParameterTypes();


        if (types.length == 0 || !method.getParameterTypes()[types.length - 1].equals(DataHandler.class)) {
            throw new MethodDefinitionException(
                    String.format("method's param should continues DataHandler, in interface : {}, method : {}",
                            iface.getName(), method.getName()));
        }
        Type t = null;
        if ((t = types[types.length - 1]) instanceof ParameterizedType) {
            Type[] handleTypes = ((ParameterizedType) t).getActualTypeArguments();
            actualType = handleTypes[0];
        }
    }

}
