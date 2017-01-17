package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 2017/1/13.
 * 纯使用sl4j的API
 */
public class Start {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger(Start.class);
        /**
         * 当没有任何配置文件发现，logback会在root logger上添加ConsoleAppender
         * 所以下面这句话，如果没有指定logback的默认配置文件，就会在控制台输出
         */
        logger.debug("start logback");
    }
}
