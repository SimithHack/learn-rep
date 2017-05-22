package com.xie.learn.springamqp.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.rabbitmq.http.client.Client;
import org.springframework.amqp.core.AmqpManagementOperations;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitManagementTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by xfq on 17/3/13.
 * 要确保RabbitMQ的管理插件启用
 * 这套API用来监控和配置broker
 * 可以查看AmqpManagementOperations接口，查看提供了哪些接口
 */
@RestController
@RequestMapping("/restapi")
public class RestAPIController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @RequestMapping("/list")
    public String apis(){
        Method[] methods = AmqpManagementOperations.class.getDeclaredMethods();
        final JsonArray items = new JsonArray();
        Arrays.stream(methods).forEach(me->{
            StringBuilder sb = new StringBuilder();
            sb.append(me.getReturnType())
                    .append(" ")
                    .append(me.getName())
                    .append("(");

            Parameter[] ps=me.getParameters();
            for(Parameter p : ps){
                sb.append(p.getType().getSimpleName()).append(" ").append(p.getName());
            }
            sb.append(")");
            JsonElement item = new JsonPrimitive(sb.toString());
            items.add(item);
        });
        return items.toString();
    }

    /**
     * 例子
     * @param exchangeName
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    @RequestMapping("/add/{exchangeName}")
    public String example(@PathVariable("exchangeName")String exchangeName) throws MalformedURLException, URISyntaxException {
        Client client = new Client("http://172.16.132.145:15672/api/","admin","admin");
        RabbitManagementTemplate managementTemplate = new RabbitManagementTemplate(client);
        managementTemplate.addExchange(ExchangeBuilder.directExchange(exchangeName).build());
        return exchangeName;
    }
}
