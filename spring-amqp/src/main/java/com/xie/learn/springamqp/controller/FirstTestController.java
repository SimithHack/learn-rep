package com.xie.learn.springamqp.controller;

import com.xie.learn.springamqp.service.FirstTestService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by xfq on 17/2/28.
 */
@RestController
@RequestMapping("/first/test")
public class FirstTestController {
    @Autowired
    private AmqpAdmin amqpAdmin;
    /**
     * 第一次测试连接服务
     */
    @Autowired
    private FirstTestService testService;

    /**
     * 生产消息
     * @param message
     * @return
     */
    @RequestMapping("/produce")
    public String firstProduce(String message){
        testService.produceMsg(message);
        return message;
    }

    /**
     * 消费消息
     * @return
     */
    @RequestMapping("/consume")
    public String firstConsume(){
        return testService.consume();
    }

    /**
     * 定义匿名queue
     * 非broker定义的queue
     * 匿名queue的应用场景是：名称唯一，独享的，自动删除
     * @return
     */
    @Autowired
    @RequestMapping("/anonymous")
    public String anonymousQueue(){
        //下面是使用broker定义的queue，不是匿名queue，broker会自动产生queue_name 前缀为spring.gen-{uuid}
        //这种方式定义的queue，如果broker连接失败，重启的时候名称会重新生成
        amqpAdmin.declareQueue(QueueBuilder.nonDurable().build());
        //匿名queue的定义方式，可以在构造方法中提供AnonymousQueue.NamingStrategy命名策略
        //系统重启创建后，queue名称不变
        Queue queue = new AnonymousQueue(new AnonymousQueue.NamingStrategy(){

            @Override
            public String generateName() {
                return "xfq-"+ UUID.randomUUID();
            }

        });
        amqpAdmin.declareQueue(queue);
        return "ok";
    }
}
