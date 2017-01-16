package com.xie.learn.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/16.
 * 测试BasicConfigurator的使用
 */
public class TestBasicDefaultConfigure {
    public static final Logger logger = LoggerFactory.getLogger(TestBasicDefaultConfigure.class);
    public static void main(String[] args){
        logger.info("进入应用程序");
        Foo foo = new Foo();
        foo.doIt();
        logger.info("退出程序");
        //打印状态信息
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(context);
    }
}
class Foo {
    public static final Logger logger = LoggerFactory.getLogger(Foo.class);
    public void doIt(){
        logger.debug("干TMD");
    }
}