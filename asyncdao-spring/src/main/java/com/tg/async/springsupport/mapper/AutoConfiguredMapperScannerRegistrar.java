package com.tg.async.springsupport.mapper;

import com.tg.async.springsupport.annotation.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * Created by twogoods on 2018/8/27.
 */
@Configuration
public class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(AutoConfiguredMapperScannerRegistrar.class);
    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.debug("Searching for mappers annotated with @Mapper");
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        try {
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            String[] packages;
            Environment env = beanFactory.getBean(Environment.class);
            String basePackages = env.getProperty("async.dao.basePackages");
            if (StringUtils.isEmpty(basePackages)) {
                packages = StringUtils.toStringArray(AutoConfigurationPackages.get(this.beanFactory));
            } else {
                packages = basePackages.split(",");
            }
            scanner.setAnnotationClass(Mapper.class);
            scanner.registerFilters();
            scanner.doScan(packages);
        } catch (IllegalStateException ex) {
            log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
