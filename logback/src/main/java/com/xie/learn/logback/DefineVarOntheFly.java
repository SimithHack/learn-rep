package com.xie.learn.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfq on 17/1/23.
 */
public class DefineVarOntheFly {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DefineVarOntheFly.class);

    public static void main(String[] args){
        logger.info("DefineVarOntheFly");
    }

}


