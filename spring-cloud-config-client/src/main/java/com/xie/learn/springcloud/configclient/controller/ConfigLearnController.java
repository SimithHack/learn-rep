package com.xie.learn.springcloud.configclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xfq on 17/2/9.
 * 学习配置spring-cloud
 */
@RestController
@RequestMapping("/config")
public class ConfigLearnController {
    /**
     * 从远程配置服务器上获取属性值
     * @return
     */
    @Autowired
    private Environment environment;
    @RequestMapping("/prop/remote/{propName}")
    public String testGetPropFromRemoteServer(@PathVariable("propName") String propName){
        return environment.getProperty(propName);
    }
}
