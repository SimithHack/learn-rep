package com.xie.learn.springcloud.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Collections;

/**
 * Created by xfq on 17/2/6.
 * 自定义属性源
 * 只需要实现PropertySourceLocator接口的bean就可以了
 * 如果需要达成jar包，那么要做META-INF/spring.factories里添加
 *  org.springframework.cloud.bootstrap.BootstrapConfiguration=com.xie.learn.springcloud.config.CustomPropertySource
 */
@Configuration
public class CustomPropertySource implements PropertySourceLocator{
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(CustomPropertySource.class);

    @Override
    public PropertySource<?> locate(Environment environment) {
        logger.info("配置自定义属性源");
        //传入的Environment对象是将要创建的ApplicationContext
        //可以通过Environment获取已经提供的属性（spring.application.name)
        return new MapPropertySource(
                "customProperty",
                Collections.<String,Object>singletonMap("com.xie.name","hahaha")
                );
    }
}
