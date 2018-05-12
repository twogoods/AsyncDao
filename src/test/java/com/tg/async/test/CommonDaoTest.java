package com.tg.async.test;

import com.tg.async.mapper.CommonDao;
import com.tg.async.mapper.User;
import com.tg.async.mysql.AsyncConfig;
import com.tg.async.mysql.AsyncDaoFactory;
import com.tg.async.mysql.pool.PoolConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by twogoods on 2018/5/4.
 */
public class CommonDaoTest {

    private CommonDao commonDao;

    private CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void init() throws Exception {
        AsyncConfig asyncConfig = new AsyncConfig();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        asyncConfig.setPoolConfiguration(configuration);
        asyncConfig.setMapperPackages("com.tg.async.mapper");
        asyncConfig.setXmlLocations("");
        AsyncDaoFactory asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);
        commonDao = asyncDaoFactory.getMapper(CommonDao.class);
    }


    @Test
    public void query() throws Exception {
        User user = new User();
        user.setUsername("ha");
        user.setAge(10);

        commonDao.query(user, users -> {
            System.out.println("result: " + users);
            latch.countDown();
        });
        latch.await();
    }

    @Test
    public void queryParam() throws Exception {
        commonDao.queryParam("ha",10, users -> {
            System.out.println("result: " + users);
            latch.countDown();
        });
        latch.await();
    }


    @Test
    public void querySingle() throws Exception {
        User user = new User();
        user.setId(1L);

        commonDao.querySingle(user, item -> {
            System.out.println(item);
            latch.countDown();
        });
        latch.await();
    }

    @Test
    public void querySingleMap() throws Exception {
        User user = new User();
        user.setId(1L);

        commonDao.querySingleMap(user, map -> {
            System.out.println(map);
            latch.countDown();
        });
        latch.await();
    }

    @Test
    public void insert() throws Exception {
        User user = new User();
        user.setUsername("insert");
        user.setAge(36);
        user.setOldAddress("BJ");
        user.setNowAddress("HZ");
        commonDao.insert(user, id -> {
            System.out.println("insert id :" + id);
            latch.countDown();
        });
        latch.await();
    }

    @Test
    public void update() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setPassword("1234");
        user.setAge(28);

        commonDao.update(user, count -> {
            System.out.println("affect count :" + count);
            latch.countDown();
        });
        latch.await();
    }

    @Test
    public void delete() throws Exception {
        User user = new User();
        user.setId(3L);

        commonDao.delete(user, count -> {
            System.out.println("affect count :" + count);
            latch.countDown();
        });
        latch.await();
    }

}
