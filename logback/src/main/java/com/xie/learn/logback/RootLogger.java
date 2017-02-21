package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 2017/1/13.
 * 获取root logger的名称
 */
public class RootLogger {
    /**
     * 日志记录
     */
    private static Logger logger = LoggerFactory.getLogger(RootLogger.class);

    public static void main(String[] args){
        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        //就是ROOT
        logger.debug(root.getName());
    }
}
