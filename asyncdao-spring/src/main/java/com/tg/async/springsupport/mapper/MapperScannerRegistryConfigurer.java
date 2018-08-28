package com.tg.async.springsupport.mapper;

import com.tg.async.springsupport.annotation.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * Created by twogoods on 2018/8/28.
 */
public class MapperScannerRegistryConfigurer implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(MapperScannerRegistryConfigurer.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        log.debug("Searching for mappers annotated with @Mapper");

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(beanDefinitionRegistry);
        try {
            if (this.applicationContext != null) {
                scanner.setResourceLoader(this.applicationContext);
            }
            List<String> packages = AutoConfigurationPackages.get(this.applicationContext);
            scanner.setAnnotationClass(Mapper.class);
            scanner.registerFilters();
            scanner.doScan(StringUtils.toStringArray(packages));
        } catch (IllegalStateException ex) {
            log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
