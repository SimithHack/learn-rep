package com.xie.learn.springamqp.controller;

import com.xie.learn.springamqp.service.FirstTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xfq on 17/2/28.
 */
@RestController
@RequestMapping("/first/test")
public class FirstTestController {
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
}
