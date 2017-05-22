package com.xie.learn.springamqp.controller;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xfq on 17/3/13.
 */
@RestController
@RequestMapping("/exchanges")
public class ExchangeController {
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 延时消息exchange 插件
     * 目前是试验中-rabbitmq 3.6.0 插件版本为0.0.1
     * 设置delayed属性为true
     * 首先要确保此插件已经安装
     */
    @RequestMapping("/delayed")
    public String delayedExchange(){
        Exchange exchange = ExchangeBuilder.directExchange("delayed.exchange").delayed().build();
        amqpAdmin.declareExchange(exchange);
        //发送delayed消息
        MessageProperties prop = new MessageProperties();
        prop.setDelay(1000);
        rabbitTemplate.send(exchange.getName(),"test.exchange.delayed.msg",
                MessageBuilder.withBody("foo".getBytes()).andProperties(prop).build());
        //检查消息是否为delayed
        prop.getReceivedDelay();
        return "ok";
    }
}
