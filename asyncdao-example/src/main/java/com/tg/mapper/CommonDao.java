package com.tg.mapper;

import com.tg.async.annotation.*;
import com.tg.async.base.DataHandler;
import com.tg.async.constant.Criterions;
import com.tg.async.constant.SqlMode;
import com.tg.async.springsupport.annotation.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by twogoods on 2018/3/23.
 */
@Mapper
@Sql(User.class)
public interface CommonDao {

    /**
     * 1. api 改异步模式，对象映射
     * 2. 注解形式表达动态sql
     * 3. 无法简单表达的sql可以手写
     * 4. 事务实现，编程式和声明式实现
     * <p>
     * 参考：dbutils mybatis ognl
     */
    @Select(columns = "id,age,username")
    @OrderBy("id desc")
    @Page(offsetField = "offset", limitField = "limit")
    @ModelConditions({
            @ModelCondition(field = "username", criterion = Criterions.EQUAL),
            @ModelCondition(field = "maxAge", column = "age", criterion = Criterions.LESS),
            @ModelCondition(field = "minAge", column = "age", criterion = Criterions.GREATER)
    })
    void query(UserSearch userSearch, DataHandler<List<User>> handler);


    @Select(columns = "age,username")
    @OrderBy("id desc")
    void queryParam(@Condition String username,
                    @Condition(criterion = Criterions.GREATER) Integer age,
                    @OffSet int offset,
                    @Limit int limit,
                    DataHandler<List<User>> handler);


    @Select(columns = "username,age", sqlMode = SqlMode.COMMON)
    void queryList(@Condition(criterion = Criterions.IN, column = "id") int[] ids, DataHandler<List<User>> handler);

    void querySingle(User user, DataHandler<User> handler);

    void querySingleMap(User user, DataHandler<Map> handler);

    @Select(columns = "id")
    void querySingleColumn(DataHandler<List<Long>> handler);

    @Count
    void count(DataHandler<Integer> handler);

    //@Insert(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user, DataHandler<Long> handler);

    @Update
    @ModelConditions(@ModelCondition(field = "id"))
    void update(User user, DataHandler<Long> handler);

    @Delete
    @ModelConditions(@ModelCondition(field = "id"))
    void delete(User user, DataHandler<Long> handler);
}
