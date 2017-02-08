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
* 如果git的label包含 "/" ,那么配置文件使用 "_" 代替。

### git url的占位符
可以在git的uri上使用{application},{label},{profile}三种占位符，因此，可以为不同的应用配置不同的git配置文件仓库
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/{application}
```
* 上例中，每个应用具有自己的配置仓库

## 模式匹配和多仓库
{application}/{profile}的模式
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: foo,bar* #表示不仅从顶层目录搜索，还从 foo/ 子文件夹，和任何以 "bar" 开头的子文件夹
          username: xie # 如果远程仓库启用了basic认证
          password: fq
          repos:
            simple: https://github.com/simple/config-repo
            development:
              pattern:
                - */development
                - */staging
              uri: https://github.com/development/config-repo
            staging:
              pattern:
                - */qa
                - */production
                - local* #{application}以local开头的所有profile
              uri: https://github.com/staging/config-repo
              cloneOnStart: true #表示服务启动就复制此远程仓库的信息
```
* 如果{application}/{profile}不匹配任何repos定义的通配符，就使用spring.cloud.config.server.git.uri
* 如果不需要指定模式，只指定uri。可以像simple那样的写法
* pattern本身是一个数组，所以 可以指定多个匹配模式
* **\*/development** 表示匹配profile为development的，application为任意的仓库地址
* 默认情况下，配置服务器会在第一次请求时从远程仓库中复制配置信息。配置服务器可以配置在启动的时候就复制远程仓库。
* searchPaths 同时还支持占位符，比如 **searchPaths: '{application}'**
* 默认情况下，配置服务器会把远程仓库的配置信息复制到临时文件夹中，可以通过 spring.cloud.config.server.git.basedir来指定自定义目录

## 还可以使用文件系统来作为配置仓库
spring.cloud.config.server.native.searchLocations 来指定，同时spring.profiles.active=native 和 使用file:前缀（默认
使用的类路径）

* 配置服务器的application.properties并不会暴露给客户端，因为在为客户端提供服务之前，这些信息都会被删除

## 为所有的应用程序配置全局信息
配置仓库中所有以 application* 开始的配置文件都会作为全局信息对所有客户端可见，这些信息当然会被具体应用相关的配置信息覆盖。

## 属性覆盖
配置服务器有一个特性：允许设置一些不可被客户端不经意覆盖的全局属性
```
spring:
  cloud:
    config:
      server:
        overrides: #把这些属性全部都放到这里就够了
          foo: bar
```
