package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/19.
 * 重复日志记录
 * 日志不仅会输出到本身的所有appender，还会输出到它父loggers的所有logger
 * 这个例子中，自身的logger配置输出到console，而root logger也输出到console
 */
public class DuplicateLogTest {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DuplicateLogTest.class);

    public static void main(String[] args){
        logger.info("must be duplicate");
    }
}
