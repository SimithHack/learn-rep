package com.xie.learn.logback;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/18.
 * 编程方式启用packagingData
 */
public class PackgingDataEnable {
    public static void main(String[] args){
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.setPackagingDataEnabled(true);
    }
}
