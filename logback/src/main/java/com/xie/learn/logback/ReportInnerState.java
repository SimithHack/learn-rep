package com.xie.learn.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 2017/1/13.
 * logback可以使用内建的状态系统报告它自己内部的状态
 * StatusMananger可以访问logback生命周期里的重要事件
 */
public class ReportInnerState {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger(Start.class);
        logger.debug("Hello");
        /**
         * 答应内部状态
         */
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        //状态打印器
        StatusPrinter.print(lc);
        /**
         * 输出：
         22:49:10,194 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
         22:49:10,195 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
         22:49:10,195 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.xml]
         22:49:10,202 |-INFO in ch.qos.logback.classic.BasicConfigurator@5010be6 - Setting up default configuration.
         */
    }
}
