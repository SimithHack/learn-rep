package com.xie.learn.springamqp.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xfq on 17/2/28.
 * spring-amqp配置
 */
@Configuration
public class RabbitConfiguration {
    /**
     * 连接工厂
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("172.16.132.145");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
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
        return new RabbitTemplate(connectionFactory());
    }

    /**
     * amqptemplate标准接口工具类
     * @return
     */
    @Bean
    public AmqpTemplate amqpTemplate(){
        return rabbitTemplate();
    }
}
