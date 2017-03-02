package com.xie.learn.springamqp.service;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xfq on 17/2/28.
 */
@Service
public class FirstTestServiceImpl implements FirstTestService {
    private String queueName="first-test";
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private AmqpAdmin amqpAdmin;

    /**
     * 向默认exchange里发送消息
     * @param message
     */
    @Override
    public void produceMsg(String message) {
        Queue queue = QueueBuilder.nonDurable(queueName).build();
        amqpAdmin.declareQueue(queue);
        MessageProperties properties = MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setMessageId("123")
                .setHeader("bar","baz")
                .build();
        Message msg = MessageBuilder.withBody(message.getBytes())
                .andProperties(properties)
                .build();
        amqpTemplate.send(queueName,msg);
    }

    @Override
    public String consume() {
        String msg = (String)amqpTemplate.receiveAndConvert(queueName);
        return msg;
    }
}
