* 介绍
>framework 框架 meessage-driven 消息驱动 microservice applications 微服务应用程序 spring-boot production-grade(工业级）应用。
spring-integration连接消息代理。
@EnableBinding类，直接连接到message broker。
@StreamListener方法上

* 核心概念
. spring cloud stream应用模型
. Binder
. pub-sub持久化支持
. 消费组支持
. 分区支持
. 可插件式Binder

* 应用模式
. 应用程序通过input/output channel同外界交互
. channels通过Binder中间件同外部broker连接

* Binder抽象
. Binder抽象实现有 kafka,rabbitmq,redis和Gemfire
. Binder的测试支持
. 可使用扩展API实现自己的Binder
. 使用spring-boot的自动配置，让spring-boot更灵活的连接broker，运行时刻决定输出到哪种broker。
  spring.cloud.stream.bindings.input.destination
. 自动检测类路径下使用的binder。可多个binder，在运行时刻选择。

* pub-sub持久化支持
.