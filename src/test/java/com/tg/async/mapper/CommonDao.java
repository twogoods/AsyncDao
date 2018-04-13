package com.tg.async.mapper;

import com.tg.async.annotation.ModelCondition;
import com.tg.async.annotation.ModelConditions;
import com.tg.async.annotation.Page;
import com.tg.async.annotation.Select;
import com.tg.async.base.DataHandler;
import com.tg.async.constant.Criterions;

import java.util.List;

/**
 * Created by twogoods on 2018/3/23.
 */
public interface CommonDao {

    /**
     * 1. api 改异步模式，对象映射
     * 2. 注解形式表达动态sql
     * 3. 无法简单表达的sql可以手写
     * 4. 事务实现，编程式和声明式实现
     * <p>
     * 参考：dbutils mybatis ognl
     */
    @Select
    @Page
    @ModelConditions({
            @ModelCondition(field = "username", criterion = Criterions.EQUAL),
            @ModelCondition(field = "minAge", column = "age", criterion = Criterions.GREATER),
            @ModelCondition(field = "maxAge", column = "age", criterion = Criterions.LESS)
    })
    void query(User user, DataHandler<List<User>> handler);
}
