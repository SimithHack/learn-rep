package com.xie.learn.springamqp.controller;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xfq on 17/3/14.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    /**
     * 连接工厂
     */
    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建事务管理器
     */
    public void rabbitTransactionManager(){
        RabbitResourceHolder resourceHolder = ConnectionFactoryUtils.getTransactionalResourceHolder(connectionFactory, true);
        //resourceHolder.addChannel();
        RabbitTransactionManager manager = new RabbitTransactionManager(connectionFactory);
    }
    /**
     * 事务使用方式-使用spring的事务管理器
     * 但是RabitTemplate的channelTransacted属性需要配置为true
     */
    @Transactional
    public void externSpringTransactionManager(){
        rabbitTemplate.setChannelTransacted(true);
        //这里边锁的事情，如果有异常发生，数据库回滚，rabbitmq的消息也会返回给broker
    }
}
