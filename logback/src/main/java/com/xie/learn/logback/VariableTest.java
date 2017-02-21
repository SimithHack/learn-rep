package com.xie.learn.logback;

/**
 * Created by xfq on 17/1/19.
 * logback可用定义变量替换，变量可用定义在配置文件中，其他外部文件中，或者其他外部资源文件中
 * 变量甚至可以在运行时刻计算而来
 * 变量的标准形式 ${name}
 */
public class VariableTest {
}
/**
 <!--定义变量-->
 <!-- 配置文件中定义变量-->
 <configuration>
    <property name="USER_HOME" value="/home/sebastien" />
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <!-- 变量引用 -->
        <file>${USER_HOME}/myApp.log</file>
        <encoder>
            <pattern>%msg%n</pattern>
        </encoder>
    </appender>
    ...
 </configuration>
 <!-- 系统变量 -->
 java -DUSER_HOME="/home/sebastien" MyApp2
 <!-- 外部文件 -->
 <configuration>
    <property file="src/main/java/chapters/configuration/variables1.properties" />
 </configuration>
 <!--从资源文件-->
 <configuration>
    <property resource="resource1.properties" />
 </configuration>
 <!--变量的作用域-->
 */