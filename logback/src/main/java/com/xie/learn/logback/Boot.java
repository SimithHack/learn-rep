package com.xie.learn.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by xfq on 17/1/18.
 * 启动程序
 */
@SpringBootApplication
public class Boot {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(Boot.class);

    public static void main(String[] args){
        SpringApplication.run(Boot.class, args);
        logger.info("Spring Boot Already Running !");
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        StatusPrinter.print(context);
    }
}
