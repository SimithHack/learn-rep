package com.xie.learn.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xfq on 17/2/6.
 * 测试和Environment相关的知识点
 */
@RestController
@RequestMapping("/environment")
public class EnvironmentController {

    /**
     * 测试自定义属性源
     */
    @Value("${com.xie.name}")
    private String xieName;
    @RequestMapping("/custom_prop")
    public String getCustomProperty(){
        return xieName;
    }

    /**
     * 可以用来刷新@RefreshScope的bean
     * 让他们重新初始化
     */
    @Autowired
    private RefreshScope scope;
    public void refreshScope(){
        scope.refreshAll();
    }
}
