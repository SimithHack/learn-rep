package com.xie.learn.springcloud.configclient.controller.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * Created by xfq on 17/4/1.
 * @EnableBinding可以接收（1+）个接口参数，这些接口定义输入输出channel。
 * spring提供了Source,Sink,Processor接口。
 *  @Input定义输入channel，消息从这个入口进入应用程序
 *  @Output接收一个channel的名称作为参数，如果不指定，那么使用被注解的方法的名称
 * spring会自动创建此接口的实例
 */
@EnableBinding(Sink.class)
public class TimerSource {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TimerSource.class);

    /**
     * @StreamListener方法注解，接收并处理stream的消息事件
     * @param vote
     */
    @StreamListener(Sink.INPUT)
    public void processVote(String vote){
        logger.info("wocao={}",vote);
    }
}
