package com.xie.learn.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusManager;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/18.
 * 添加状态监听器
 */
public class AddStatusListener {

    public static void main(String[] args){
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        //状态管理器
        StatusManager statusManager = lc.getStatusManager();
        //所有状态数据输出到console
        OnConsoleStatusListener onConsoleListener = new OnConsoleStatusListener();
        statusManager.add(onConsoleListener);
        //可同时注册多个
    }
}
