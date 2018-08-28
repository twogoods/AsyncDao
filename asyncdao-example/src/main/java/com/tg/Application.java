package com.tg;

import com.tg.async.springsupport.annotation.EnableAsyncDao;
import com.tg.mapper.CommonDao;
import com.tg.mapper.UserSearch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by twogoods on 2018/8/27.
 */
@SpringBootApplication
@EnableAsyncDao
public class Application {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(Application.class);

        CommonDao commonDao = applicationContext.getBean(CommonDao.class);
        UserSearch userSearch = new UserSearch();
        userSearch.setUsername("ha");
        userSearch.setMaxAge(28);
        userSearch.setMinAge(8);
        userSearch.setLimit(5);

        commonDao.queryList(new int[]{1, 2}, users -> {
            System.out.println("result: " + users);
        });

        commonDao.query(userSearch, users -> {
            System.out.println("result: " + users);
        });

    }
}
