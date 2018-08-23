package com.tg.async.test;

import com.tg.async.mapper.CommonDao;
import com.tg.async.mapper.User;
import com.tg.async.mysql.AsyncConfig;
import com.tg.async.mysql.AsyncDaoFactory;
import com.tg.async.mysql.SQLConnection;
import com.tg.async.mysql.Translaction;
import com.tg.async.mysql.pool.PoolConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by twogoods on 2018/8/23.
 */
public class TranslationTest {

    private AsyncDaoFactory asyncDaoFactory;

    private CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void init() throws Exception {
        AsyncConfig asyncConfig = new AsyncConfig();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        asyncConfig.setPoolConfiguration(configuration);
        asyncConfig.setMapperPackages("com.tg.async.mapper");
        asyncConfig.setXmlLocations("");
        asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);
    }

    @Test
    public void test() throws InterruptedException {
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
                translaction.commit(Void -> {
                    latch.countDown();
                });
            });

        });
        latch.await();
    }


    @Test
    public void testBase() throws Exception {
        AsyncConfig asyncConfig = new AsyncConfig();
        PoolConfiguration configuration = new PoolConfiguration("root", "localhost", 3306, "admin", "test");
        asyncConfig.setPoolConfiguration(configuration);
        AsyncDaoFactory asyncDaoFactory = AsyncDaoFactory.build(asyncConfig);

        asyncDaoFactory.getConfiguration().getConnectionPool().getConnection(asyncConnection -> {
            SQLConnection connection = asyncConnection.result();
            connection.setAutoCommit(false, voidAsyncResult -> {
                connection.execute("insert into T_test (name,age) values('haha',23)", result -> {
                    connection.rollback(rollback -> {
                        System.out.println("rollback");
                        connection.close();
                        latch.countDown();
                    });
                });
            });
        });
        latch.await();
    }
}
