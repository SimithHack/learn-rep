# 介绍
本笔记主要介绍Spring-cloud的配置细节
> spring cloud提供客户端和服务端的配置集中化支持，client等同于Spring中的**Environment**概念，而
server等同于**PropertySource**概念,默认使用git作为配置文件仓库，因此可以很容易根据git的label切换
不同的配置文件版本（eg. dev,test,release)

## 资源文件命名格式
```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```
其中：
* application = spring.config.name
* profile = 当前的偏好设置**","**分隔的属性值
* label = git的label 默认是master

spring-cloud **server** 从git库中为客户端提供配置信息，配置为spring.cloud.config.server.git.uri:{git_uri}

## 客户端的用法
创建一个spring-boot应用程序，将spring-cloud-config-client添加到类路径下（maven使用spring-cloud-starter-parent）
客户端启动后，会默认到localhost:8888端口去获取外部配置属性，可以修改spring.cloud.config.uri:{config_server_uri}去
改变此行为。

* 客户端启动的属性可以通过**/env**节点查询

## 服务器端配置
提供基于HTTP的面向资源的API，为外部提供配置服务。

* 使用@EnableConfigServer可以将一个spring-boot应用转变为配置服务器
* 注意server的启动端口，客户端默认使用**8888**来连接，而spring-boot默认启动端口为**8080**
* 需要依赖spring-cloud-config-server包

## 配置仓库
EnvironmentRepository决定着配置数据存放在何处，Environment的资源由三个属性决定

* {application} spring.application.name
* {profile} spring.profiles.active **","**分隔，多个profile配置相同的属性，最后一个有效
* {label} 配置文件的版本

优先顺序：{application}.yml < {application}-{profile}.yml

## git配置仓库
默认EnvironmentRepository的实现是GIT，升级方便。

* **file:** 前缀表示从本地仓库取，它也是一个git本地仓库地址，但是不会从远处仓库clone，也不会对远处仓库造成影响。
* 为了扩展配置服务器，需要将所有的配置服务器的实例的仓库都指向相同的仓库地址。

