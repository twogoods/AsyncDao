package com.tg.async.springsupport.annotation;

import com.tg.async.springsupport.config.AsyncDaoAutoConfiguration;
import com.tg.async.springsupport.mapper.AutoConfiguredMapperScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by twogoods on 2018/8/27.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AsyncDaoAutoConfiguration.class, AutoConfiguredMapperScannerRegistrar.class})
@Documented
public @interface EnableAsyncDao {
}
