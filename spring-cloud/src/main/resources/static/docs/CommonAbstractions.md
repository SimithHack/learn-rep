# 介绍
spring-cloud的常用抽象层
* 服务发现
* 负载均衡
* 断路器
所有的spring-cloud的客户端都可以自由使用

## LoadBanlanced RestTemplate
```
//使用Ribbon创建全路径的物理地址
@LoadBalanced
@Bean
RestTemplate restTemplate() {
    return new RestTemplate();
}
```

## 忽略网络接口
忽略一些命名的网络接口可以在服务注册的时候排除使用，可以使用**正则表达式**语法
```
spring:
  cloud:
    inetutils:
      ignoredInterfaces:
        - docker0 #docker0接口
        - veth.* #所有已veth开始的网络接口排除
```
当然也可以指定特定的网络接口
```
spring:
  cloud:
    inetutils:
      preferredNetworks:
        - 192.168
        - 10.0
```
也可以指定只使用本地地址
```
spring:
  cloud:
    inetutils:
      useOnlySiteLocalInterfaces: true
```
