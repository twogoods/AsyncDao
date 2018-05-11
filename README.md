## AsyncDao
asyncDao是一款异步非阻塞模型下的数据访问层工具。
* MySQL only. 基于MySQL的[异步驱动](https://github.com/mauricio/postgresql-async)
* 借鉴了Mybatis的mapping 和 dynamicSQL的内容，Mybatiser可以无缝切换
* 注解表达SQL的能力（**待完成**）
* 事务支持（**待完成**）

使用上与Mybatis几乎一致，由于异步非阻塞的关系，数据的返回都会通过回调DataHandler来完成，所以方法定义参数的最后一个一定是DataHandler类型


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
mapper.xml与Mybatis几乎一致的写法

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


    <insert id="insert" parameterType="com.tg.test.User" useGeneratedKeys="true" keyProperty="id">insert into T_User
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

    <update id="update" parameterType="com.tg.test.User">
        update T_User
        <set>
            <if test="user.password != null">password=#{user.password},</if>
            <if test="user.age != null">age=#{user.age},</if>
        </set>
        where id = #{user.id}
    </update>
</mapper>
```
使用

```
AsyncConfig asyncConfig = new AsyncConfig();
PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
asyncConfig.setPoolConfiguration(configuration);
asyncConfig.setMapperPackages("com.tg.async.mapper");//mapper接口
asyncConfig.setXmlLocations("/mapper");//xml目录
AsyncDaoFactory asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);
CommonDao commonDao = asyncDaoFactory.getMapper(CommonDao.class);
   
User user = new User();
user.setUsername("ha");
user.setAge(10);
CountDownLatch latch = new CountDownLatch(1);
commonDao.query(user, users -> {
  System.out.println(users);
  latch.countDown();
});
latch.await();
                
```