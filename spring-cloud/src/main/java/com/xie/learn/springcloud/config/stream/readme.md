## spring-cloud-stream说明
* 构建消息驱动的微服务应用程序
* 支持多个中间件
* 提出 持久化 publish-subscribe语义，消费组，和分区的概念

## @EnableBinding
* @EnableBinding(Sink.class) 指定声明输入输出channel的接口
* @StreamListener 将方法声明为消息监听器
* spring提供Sink,Source和Processor接口
* @Input定义消息接收入口channel
* @Output 定义产生消息的发送出口channel
* Spring自动为接口产生实现类，只需要@Autowired

## 主要概念
> Spring提过了许多抽象和原型来简化基于消息的微服务应用程序开发

* stream应用模型
* Binder抽象
* 持久化的pub-sub支持
* 消费组支持
* 分区支持
* 插件式的binder API

### 应用模型
>应用程序通过 input/output channels同binder连接，binder又真正的和外部的消息
中间件通信。

![应用模型](http://cloud.spring.io/spring-cloud-static/Brixton.SR5/images/SCSt-with-binder.png)

### Binder抽象
* 支持 kafka,rabbitmq,redis和gemfire
* 提供TestSupportBinder，专为测试，不修改broker的实际状态。
* 开发者可以在运行时刻选择broker的类型（从kafka的topic还是从mq的exchange）。
* 支持任何spring-boot所支持的配置文件
* 自动在类路径下检测使用哪一种broker来bind。
* 不同的中间件可使用同一套代码。

## 持久化 pub-sub
