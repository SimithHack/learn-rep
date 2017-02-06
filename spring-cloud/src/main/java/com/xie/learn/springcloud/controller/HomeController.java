package com.xie.learn.springcloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xfq on 17/2/4.
 */
@RestController
public class HomeController {

    @RequestMapping("/")
    public String home(){
        return "Hello World!";
    }
}
