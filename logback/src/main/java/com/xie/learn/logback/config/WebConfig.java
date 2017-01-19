package com.xie.learn.logback.config;

import ch.qos.logback.access.ViewStatusMessagesServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Created by xfq on 17/1/18.
 * 配置查看logback状态消息的servlet
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    /**
     * 注册logback的ViewStatusMessagesServlet
     * 此servlet可以查看StatusManager的输出数据
     * @return
     */
    @Bean
    public ServletRegistrationBean registLogbackViewServlet(){
        ViewStatusMessagesServlet messagesServlet = new ViewStatusMessagesServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(messagesServlet);
        registrationBean.addUrlMappings("/status");
        registrationBean.setName("ViewStatusMessages");
        return registrationBean;
    }
}
