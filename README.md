## AsyncDao
asyncDao是一款异步非阻塞模型下的数据访问层工具。
* MySQL only. 基于MySQL的[异步驱动](https://github.com/mauricio/postgresql-async)
* 借鉴了Mybatis的mapping 和 dynamicSQL的内容，Mybatiser可以无缝切换
* 注解表达SQL的能力
* 事务支持
* SpringBoot支持

### Mybatis like
使用上与Mybatis几乎一致，由于异步非阻塞的关系，数据的返回都会通过回调DataHandler来完成，所以方法定义参数的最后一个一定是DataHandler类型。由于需要提取方法的参数名，于是需要加上编译参数`-parameters`，请将它在IDE和maven里配置上。

```
public interface CommonDao {

    void query(User user, DataHandler<List<User>> handler);

    void querySingle(User user, DataHandler<User> handler);

    void querySingleMap(User user, DataHandler<Map> handler);

    void insert(User user,DataHandler<Long> handler);

    void update(User user,DataHandler<Long> handler);

    void delete(User user,DataHandler<Long> handler);
}
```
mapper.xml与Mybatis几乎一致的写法(覆盖常见标签，一些不常用标签可能不支持，动态SQL建议使用注解SQL功能)

```
<?xml version="1.0" encoding="UTF-8"?>
<mapper namespace="com.tg.async.mapper.CommonDao">
    <resultMap id="BaseResultMap" type="com.tg.async.mapper.User">
        <id column="id" property="id"/>
        <result column="old_address" property="oldAddress"/>
        <result column="created_at" property="createdAt"/>
        <result column="password" property="password"/>
        <result column="now_address" property="nowAddress"/>
        <result column="state" property="state"/>
        <result column="age" property="age"/>
        <result column="username" property="username"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="query" resultMap="BaseResultMap">select * from T_User
        <where>
            <if test="user.username!=null and user.username!=''">AND username = #{user.username}</if>
            <if test="user.age != null">OR age > #{user.age}</if>
        </where>
        order by id desc
    </select>


    <insert id="insert" useGeneratedKeys="true" keyProperty="id">insert into T_User
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="user.oldAddress != null">old_address,</if>
            <if test="user.createdAt != null">created_at,</if>
            <if test="user.password != null">password,</if>
            <if test="user.nowAddress != null">now_address,</if>
            <if test="user.state != null">state,</if>
            <if test="user.age != null">age,</if>
            <if test="user.username != null">username,</if>
            <if test="user.updatedAt != null">updated_at,</if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="user.oldAddress != null">#{user.oldAddress},</if>
            <if test="user.createdAt != null">#{user.createdAt},</if>
            <if test="user.password != null">#{user.password},</if>
            <if test="user.nowAddress != null">#{user.nowAddress},</if>
            <if test="user.state != null">#{user.state},</if>
            <if test="user.age != null">#{user.age},</if>
            <if test="user.username != null">#{user.username},</if>
            <if test="user.updatedAt != null">#{user.updatedAt},</if>
        </trim>
    </insert>

    <update id="update">
        update T_User
        <set>
            <if test="user.password != null">password=#{user.password},</if>
            <if test="user.age != null">age=#{user.age},</if>
        </set>
        where id = #{user.id}
    </update>
</mapper>
```

### 注解SQL
在XML里写SQL对于一些常见SQL实在是重复劳动，so这里允许你利用注解来表达SQL，该怎么做呢？

#### Table与Model关联
```
@Table(name = "T_User")
public class User {
    @Id("id")
    private Long id;
    //建议全部用包装类型，并注意mysql中字段类型与java类型的对应关系，mysql的int不会自动装换到这里的long

    private String username;
    private Integer age;

    @Column("now_address")
    private String nowAddress;

    @Column("created_at")
    private LocalDateTime createdAt;
    //asyncDao 里sql的时间类型都用joda，注意不是JDK8提供的那个，而是第三方包org.joda.time

    @Ignore
    private String remrk;
```
@Table记录数据表的名字 @Id记录主键信息 @Column映射了表字段和属性的关系，如果表字段和类属性同名，那么可以省略这个注解 @Ingore忽略这个类属性，没有哪个表字段与它关联。
#### 定义接口
```
@Sql(User.class)
public interface CommonDao {
    @Select(columns = "id,age,username")
    @OrderBy("id desc")
    @Page
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

    @Insert(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user, DataHandler<Long> handler);

    @Update
    @ModelConditions(@ModelCondition(field = "id"))
    void update(User user, DataHandler<Long> handler);

    @Delete
    @ModelConditions(@ModelCondition(field = "id"))
    void delete(User user, DataHandler<Long> handler);
}
```
看到这些注解你应该能猜出来SQL长什么样，接下来解释一下这些注解
#### 查询
```
@Select(columns = "id,age,username")
@OrderBy("id desc")
@Page
@ModelConditions({
       @ModelCondition(field = "username", criterion = Criterions.EQUAL),
       @ModelCondition(field = "maxAge", column = "age", criterion = Criterions.LESS),
       @ModelCondition(field = "minAge", column = "age", criterion = Criterions.GREATER)
})
void query(UserSearch userSearch, DataHandler<List<User>> handler);
```
##### @Select
* `columns`:默认 `select *`可以配置`columns("username,age")`选择部分字段；
* `SqlMode`:有两个选择，SqlMode.SELECTIVE 和 SqlMode.COMMON，区别是selective会检查查询条件的字段是否为null来实现动态的查询，即值为null时不会成为查询条件。并且`@Select`，`@Count`，`@Update`，`@Delete`都有`selective`这个属性。

##### @Condition
* `criterion`：查询条件，`=`,`<`,`>`,`in`等，具体见`Criterions`
* `column`：与表字段的对应，若与字段名相同可不配置
* `attach`：连接 `and`,`or`， 默认是`and`
* `test`：SqlMode为selective下的判断表达式，类似Mybatis`<if test="username != null">`里的test属性，动态化查询条件

`@Limit`，`@OffSet`为分页字段。
方法的参数不加任何注解一样会被当做查询条件，如下面两个函数效果是一样的：

```
@Select()
void queryUser(Integer age,DataHandler<List<User>> handler);

@Select()
void queryUser(@Condition(criterion = Criterions.EQUAL, column = "age") Integer age,DataHandler<List<User>> handler);
```
#### 查询Model
上面的例子在查询条件比较多时方法参数会比较多，我们可以把查询条件封装到一个类里，使用`@ModelConditions`来注解查询条件，注意被`@ModelConditions`注解的方法只能有两个参数，一个是查询model，一个是DataHandler。

```
@Select
@Page
@ModelConditions({
       @ModelCondition(field = "username", criterion = Criterions.EQUAL),
       @ModelCondition(field = "minAge", column = "age", criterion = Criterions.GREATER),
       @ModelCondition(field = "maxAge", column = "age", criterion = Criterions.LESS),
       @ModelCondition(field = "ids", column = "id", criterion = Criterions.IN)
})
void queryUser5(UserSearch userSearch,DataHandler<List<User>> handler);
```
##### @ModelCondition
* `field`:必填，查询条件中类对应的属性
* `column`：对应的表字段
* `test`：动态SQL的判断表达式

`@Page`只能用在ModelConditions下的查询，并且方法参数的那个类应该有`offset`，`limit`这两个属性，或者 使用`@Page(offsetField = "offset",limitField = "limit")`指定具体字段
#### 统计
```
@Count
void count(DataHandler<Integer> handler);//返回Long类型
```
#### 插入
```
@Insert(useGeneratedKeys = true, keyProperty = "id")//返回自增id
void insert(User user, DataHandler<Long> handler);
```
#### 更新
```
@Update(columns = "username,age")//选择更新某几个列
void update(User user, DataHandler<Long> handler);//返回affectedRows
```
#### 删除
```
@Delete
int delete(@Condition(criterion = Criterions.GREATER, column = "age") int min,
          @Condition(criterion = Criterions.LESS, column = "age") int max,
          DataHandler<Long> handler);

@Delete
@ModelConditions(@ModelCondition(field = "id"))
void delete(User user, DataHandler<Long> handler);
```
### 使用
简单的编程使用

```
AsyncConfig asyncConfig = new AsyncConfig();
PoolConfiguration configuration = new PoolConfiguration("username", "localhost", 3306, "password", "database-name");
asyncConfig.setPoolConfiguration(configuration);
asyncConfig.setMapperPackages("com.tg.async.mapper");//mapper接口
asyncConfig.setXmlLocations("mapper/");//xml目录,classpath的相对路径,不支持绝对路径
AsyncDaoFactory asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);
CommonDao commonDao = asyncDaoFactory.getMapper(CommonDao.class);
   
UserSearch userSearch = new UserSearch();
userSearch.setUsername("ha");
userSearch.setMaxAge(28);
userSearch.setMinAge(8);
userSearch.setLimit(5);
CountDownLatch latch = new CountDownLatch(1);
commonDao.query(user, users -> {
  System.out.println(users);
  latch.countDown();
});
latch.await();
                
```

## 事务
Mybatis和Spring体系里有一个非常好用的`@Translactional`注解，我们知道事务本质就是依赖connection的rollback等操作，那么一个事务下多个SQL就要共用这一个connection，如何共享呢？传统的阻塞体系下ThreadLocal就成了实现这一点的完美解决方案。那么在异步世界里，要实现mybatis-spring一样的上层Api来完成事务操作是一件非常困难的事，难点就在于Api太上层，以至于无法实现connection共享。于是这里自能退而求其次，使用编程式的方式来使用事务，抽象出一个`Translaction`，具体的mapper通过`translaction.getMapper()`来获取，这样通过同一个`Translaction`得到的Mapper都将共用一个connection。

```
CountDownLatch latch = new CountDownLatch(1);
AsyncConfig asyncConfig = new AsyncConfig();
PoolConfiguration configuration = new PoolConfiguration("username", "localhost", 3306, "password", "database-name");
asyncConfig.setPoolConfiguration(configuration);
asyncConfig.setMapperPackages("com.tg.async.mapper");
asyncConfig.setXmlLocations("mapper/");
asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);
asyncDaoFactory.startTranslation(res -> {
    Translaction translaction = res.result();
    System.out.println(translaction);
    CommonDao commonDao = translaction.getMapper(CommonDao.class);
    User user = new User();
    user.setUsername("insert");
    user.setPassword("1234");
    user.setAge(28);
    commonDao.insert(user, id -> {
        System.out.println(id);
        translaction.rollback(Void -> {
            latch.countDown();
        });
    });
});
latch.await();
```

## SpringBoot
虽然Spring5推出了WebFlux，但异步体系在Spring里依旧不是主流。在异步化改造的过程中，大部分人也往往会保留Spring的IOC，而将其他交给Vertx，所以asyncDao对于Spring的支持就是将Mapper注入IOC容器。
### quick start
YAML配置文件：

```
async:
    dao:
     mapperLocations: /mapper  #xml目录,classpath的相对路径,不支持绝对路径
     basePackages: com.tg.mapper #mapper所在包
     username: username
     host: localhost
     port: 3306
     password: pass
     database: database-name
     maxTotal: 12
     maxIdle: 12
     minIdle: 1
     maxWaitMillis: 10000
```
添加`@Mapper`来实现注入

```
@Mapper
@Sql(User.class)
public interface CommonDao {
    @Select(columns = "id,age,username")
    @OrderBy("id desc")
    @Page(offsetField = "offset", limitField = "limit")
    @ModelConditions({
            @ModelCondition(field = "username", criterion = Criterions.EQUAL),
            @ModelCondition(field = "maxAge", column = "age", criterion = Criterions.LESS),
            @ModelCondition(field = "minAge", column = "age", criterion = Criterions.GREATER)
    })
    void query(UserSearch userSearch, DataHandler<List<User>> handler);
}
```
通过`@EnableAsyncDao`来开启支持，简单示例：

```
@SpringBootApplication
@EnableAsyncDao
public class DemoApplication {

    public static void main(String[] args){
        ApplicationContext applicationContext = SpringApplication.run(DemoApplication.class);
        CommonDao commonDao = applicationContext.getBean(CommonDao.class);

        UserSearch userSearch = new UserSearch();
        userSearch.setUsername("ha");
        userSearch.setMaxAge(28);
        userSearch.setMinAge(8);
        userSearch.setLimit(5);

        commonDao.query(userSearch, users -> {
            System.out.println("result: " + users);
        });
    }
}
```