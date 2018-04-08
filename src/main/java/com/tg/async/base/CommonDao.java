package com.tg.async.base;

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
     *
     * 参考：dbutils mybatis ognl
     */
    void query(User user, DataHandler<List<User>> handler);
}
