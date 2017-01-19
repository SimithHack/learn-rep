package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/19.
 * 解决默认的logger累积效应，可以在logger上配置additivity属性
 * 如果设置为false，默认为true，则日志请求就不会继续传播
 */
public class AdditiveAttrTest {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AdditiveAttrTest.class);

    public static void main(String[] args){
        logger.info("only appear to file");
        new AdditiveAttrTest().new Children().doit();
    }

    /**
     * 它的孩子日志也会终于此
     */
    class Children {
        /**
         * 日志
         */
        private final Logger logger = LoggerFactory.getLogger(Children.class);
        public void doit(){
            logger.info("children do it");
        }
    }
}
