package com.xie.learn.logback;


import ch.qos.logback.access.joran.JoranConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/18.
 * Joran提供logback默认配置，可以直接调用JoranConfigurator覆盖logback的默认配置
 */
public class JoranConfiguratorTest {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(JoranConfiguratorTest.class);

    public static void main(String[] args) throws JoranException {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        //初始化配置
        JoranConfigurator conf = new JoranConfigurator();
        conf.setContext(context);
        //清楚所有前边的配置
        context.reset();
        //使用参数传递过来的配置文件
        conf.doConfigure(args[0]);
        //输出警告或者错误信息
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);

    }
}
