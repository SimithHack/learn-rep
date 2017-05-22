package com.xie.learn.springamqp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xfq on 17/2/28.
 * spring-amqp配置
 */
@Configuration
public class RabbitConfiguration {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);
    /**
     * 连接工厂
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("192.168.233.128");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("usercenter-api-service");
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        return connectionFactory;
    }

    /**
     * AmqpAdmin Spring-AMQP抽象
     * @return
     */
    @Bean
    public AmqpAdmin amqpAdmin(){
        return new RabbitAdmin(connectionFactory());
    }

    /**
     * rabbit-mq实现操作工具类
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(apiMeesageConverter());
        return rabbitTemplate;
    }

    /**
     * amqptemplate标准接口工具类
     * @return
     */
    @Bean
    public AmqpTemplate amqpTemplate(){
        return rabbitTemplate();
    }
    /**
     * 消息转换
     * @return
     */
    @Bean
    public MessageConverter apiMeesageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
