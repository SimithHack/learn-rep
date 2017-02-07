package com.xie.learn.springcloud.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Created by xfq on 17/2/7.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerBoot {
    public static void main(String[] args){
        SpringApplication.run(ConfigServerBoot.class, args);
    }
}
