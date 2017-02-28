package com.xie.learn.springamqp;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by xfq on 17/2/28.
 */
@SpringBootApplication
@EnableRabbit
public class AmqpBoot {
    public static void main(String[] args){
        SpringApplication.run(AmqpBoot.class,args);
    }
}
