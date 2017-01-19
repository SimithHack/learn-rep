package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/19.
 * 测试Appender的继承关系
 * root的level设置为off，但是不影响它里边的appender输出
 */
public class AppenderHierarchy {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AppenderHierarchy.class);

    public static void main(String[] args){
        logger.info("test-info");
        logger.debug("test-debug");
    }
}
