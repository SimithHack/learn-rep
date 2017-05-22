package com.xie.learn.springcloud.configclient.controller.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by xfq on 17/4/1.
 */
@SpringBootApplication
@ComponentScan("com.xie.learn.springcloud.configclient.controller.stream")
public class StreamApplication {

    public static void main(String[] args){
        SpringApplication.run(StreamApplication.class,args);
    }
}