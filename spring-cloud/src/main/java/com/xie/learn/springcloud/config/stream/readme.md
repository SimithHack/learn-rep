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
* pub-sub支持
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

### 持续的pub-sub支持
* 一种优雅的编程模型

### 消费组概念
> 同一个应用程序部署多个实例，这些实例竞争使用消息池的消息。消费组针对此应用场景产生。

* spring.cloud.stream.bindings.input.group属性指定组名称。
* 每一个消费组都会获得一份消息拷贝，但是每一个组里只有一个成员可以消费。
* 如果没有具体指定组，默认分配到一个 匿名组 ，这个组里只有一个成员

![消费组概念](http://cloud.spring.io/spring-cloud-static/Brixton.SR5/images/SCSt-groups.png)

### 消费组的持久化订阅
* 消费组的订阅是持久化的，就是说，即使组中其他的成员都挂掉，只要还有一个成员可用，那么消息都会继续发送。
* 匿名组的订阅是非持久化
* 特别是这种cloud-stream类的应用程序部署多个实例后更需要为每一个Input都指定消费组。避免受到重复消息。

### 分区支持
* 应用程序的多个部署实例间进行数据分区
* 被相同标记标识的消息始终被同一个消费实例处理
* 不依赖于消息中间件是否本身支持
* 消息生产和消费方都需要做相关配置

![消息分区](http://cloud.spring.io/spring-cloud-static/Brixton.SR5/images/SCSt-partitioning.png)


## 编程模型
> spring-cloud-stream 提供一些预定义注解声明input和output通道

### @EnableBinding
```java
@Import(...)
@Configuration
@EnableIntegration
public @interface EnableBinding {
    ...
    //包含bindable组件（channel）的接口
    Class<?>[] value() default {};
}
```
* 本身是@Configuration

### @Input和@Output
* 一个接口可以同时定义多个@Input和@Output接口
* 不指定名称，默认为接口方法的名称
* @Input("自定义接口名称")

#### Source,Sink 和 Processor
> spring提供的常用接口，并且自动产生实现体

* source只有一个@Output接口
* Sink只有一个@Input接口
* Process是Source和Processor复合体 用于那些既有inbound又有outbound的应用程序

#### 注入bound接口
>spring自动产生实现类，直接注入即可

```java
//直接注入绑定接口
@Autowired
private Source source;
//或者直接注入channel
@Autowired
private MessageChannel output;
```

### 生产和消费消息
* 直接和spring-integration配合使用
* 使用@StreamListener

### @StreamListener
* 依赖于contentType使用MessageConvert来转化为相应的对象
* 接收@Payload @Headers @Header方法参数注解
* 对于有返回值的接口，需要使用@Sendto指定使用那个输出绑定

```java
@EnableBinding(Processor.class)
public class TransformProcessor {

  @Autowired
  VotingService votingService;

  @StreamListener(Processor.INPUT)
  @SendTo(Processor.OUTPUT)
  public VoteResult handle(Vote vote) {
    return votingService.record(vote);
  }
}
```

### 聚合
>spring-cloud支持将多个应用程序的Input和Output直接连接，而不经过消息中间件。但是只支持这几种应用类型。

* 只有一个输出绑定的应用程序，并且是Source接口。
* 只有一个输入绑定，并且是Sink接口。
* 或者processor
