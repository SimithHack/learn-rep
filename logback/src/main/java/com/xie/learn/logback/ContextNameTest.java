package com.xie.learn.logback;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/19.
 * 每一个日志都会绑定到一个context上，默认context的名称是"default"
 * 指定不同的contextName可用在同一个输出目的地作为区分不同应用的输出
 * 可用在logback.xml中这样配置：
 *
 *
 */
public class ContextNameTest {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ContextNameTest.class);

    public static void main(String[] args){
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        context.setName("ContextNameTest");
        logger.info("我的输出不一样吧");
    }

}
/** xml配置
<configuration>
    <!-- 指定contextName 区分不同应用输出 -->
    <contextName>myAppName</contextName>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d %contextName [%t] %level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
*/
