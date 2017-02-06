## Environment 属性改变
应用程序会监听 EnvironmentChangedEvent 事件，应用程序在收到此事件后会：
* 重新绑定@ConfigurationProperties的bean
* 为所有logging.level.* 下边的logger配置日志级别

**默认情况下，客户端是不会主动监测此事件的，一般都是广播此事件**

### Refresh Scope
被标注有@RefreshScope的bean在 EnvironmentChangedEvent 发生时会特殊处理，这主要是为了解决那些只能在初始化时进行配置的有状态bean的问题。
> 比如数据库连接池，如果更改了连接URL，已经在处理的连接继续使用老的URL，从池中新获取的连接，就是用新的URL了。

refresh scope bean 都是懒加载的，scope缓存了它初始化时候的值，如果想强制重新初始化，需要让缓存中的条目无效。
* Context中还有一个RefreshScope的bean
> refreshAll() 禁用缓存，刷新所有RefreshScope bean .
> refresh(String) 可以通过名字具体刷新哪一个bean .

* RefreshScope Bean 对@Configuration 有效，但是它并不保证在此类的所有@Bean都是满足语义的。除非此Bean本身也是@RefreshScope的。

## 属性加密和解密
{cipher}\* 使用这种形式的值，只要存在一个有效的key值，就可以在此属性使用之前自动解密。使用这个功能需要将**Spring Security RSA**(spring-security-rsa)引入到classpath下

## Endpoints
spring-cloud提供了如下几个管理节点

| 节点路径      | 意义     |
| :-------------- | :------------|
| /env       | 更新Environment 并且重新绑定@ConfigurationProperties的bean 以及它们的日志级别     |
|/refresh|重新加载bootstrap context,并且刷新@RefreshScope Beans|
|/restart|默认是禁用的，可以重新启动ApplicationContext|
|/pause或者/resume|调用ApplicationContext的stop和start方法|
