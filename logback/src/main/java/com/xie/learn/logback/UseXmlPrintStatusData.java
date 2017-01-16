package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/16.
 * 使用配置文件打印状态数据
 */
public class UseXmlPrintStatusData {
    public static final Logger logger = LoggerFactory.getLogger(UseXmlPrintStatusData.class);

    public static void main(String[] args){
        logger.info("开始了");
    }
}
