package com.xie.learn.springamqp.service;

/**
 * Created by xfq on 17/2/28.
 */
public interface FirstTestService {
    /**
     * 生产消息
     * @param message
     */
    void produceMsg(String message);

    /**
     * 消费消息
     * @return
     */
    String consume();
}
