AmqpTemplate接口覆盖了发送和接收消息的常用方法。目前就一个实现：RabbitTemplate

## 添加重试功能
从1.3开始，可以在配置RabbitTemplate使用RetryTemplate，用以解决broker的连接问题。

```
@Bean
public AmqpTemplate rabbitTemplate();
    RabbitTemplate template = new RabbitTemplate(connectionFactory());
    RetryTemplate retryTemplate = new RetryTemplate();
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(500);
    backOffPolicy.setMultiplier(10.0);
    backOffPolicy.setMaxInterval(10000);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    template.setRetryTemplate(retryTemplate);
    return template;
}
```
* 配置使用ExponentialBackOffPolicy策略，即指数级别重试间隔时间
* 默认SimpleRetryPolicy只是尝试三次。

1.4之后，还支持在RabbitTemplate配置恢复回到函数

```
retryTemplate.execute(
    new RetryCallback<Object, Exception>() {
        @Override
        public Object doWithRetry(RetryContext context) throws Exception {
            context.setAttribute("message", message);
            return rabbitTemplate.convertAndSend(exchange, routingKey, message);
        }
    }, new RecoveryCallback<Object>() {
        //在Retry环境中，回调只包含lastThrowable属性，如果要传递额外的信息，可以使用单独的RetryTemplate
        @Override
        public Object recover(RetryContext context) throws Exception {
            Object message = context.getAttribute("message");
            Throwable t = context.getLastThrowable();
            // Do something with message
            return null;
        }
    });
}
```

### 在异步发布环境中检测成功和失败请求
发布消息是异步的，默认如果消息么有被正确路由，RabbitMQ会简单的丢弃掉。正确的消息发布做法是，接收消息确认。

**消息发送失败的场景**

* 发布给Exchange，但是此Exchange并没有绑定任何Queue
* 发布给不存在的Exchange
>此场景中，消息会被丢弃，相关的channel会因为异常而关闭，不产生任何返回结果。这种异常会被日志记录.
>可以向CachingConnectionFactory注册ChannelListener获取这类情况的事件通知。

```
this.connectionFactory.addConnectionListener(new ConnectionListener() {
    @Override
    public void onCreate(Connection connection) {}
    @Override
    public void onShutDown(ShutdownSignalException signal) {
        //可以检查signal的reason属性，去判断究竟什么原因发生了异常
        ...
    }
});
```
* 如果想要在发送消息的线程中检测异常，可将RabbitTemplate.setChannelTransacted(true)，然后在txCommit()检测异常。
* 事务是很影响性能的，请酌情考虑使用。

#### 消息确认和消息返回
**消息返回**

获取返回消息，template的mandatory属性需要设置为true。或者mandatory-expression表达式计算结果为true。
这种特性需要先将CachingConnectionFactory的publisherReturns设置为true。

返回消息以RabbitTemplate.ReturnCallback回调返回。调用setReturnCallback(ReturnCallback callback)来设置回调函数
* 一个RabbitTemplate只支持一个ReturnCallback

**消息确认**

首先将CachingConnectionFactory的publisherConfirms属性配置为true，通过setConfirmCallback(ConfirmCallback callback)方法设置。
需要实现下面方法。

```
void confirm(CorrelationData correlationData, boolean ack, String cause);
```
* CorrelationData 客户端发送原始消息时附加的对象
* ack true为正确应答，false为nack应答，当为nack应答时，casue包含失败原因。
* 一个RabbitTemplate只支持一个ConfirmCallback

**注意**

>当rabbittemplate发送操作完成后，就会关闭channel，这可以避免在connectfactory的缓存满的时候接受确认和返回消息。
当缓存满了，框架会延迟channel关闭5秒，目的是给确认和返回消息足够的时间接受。如果使用确认消息，通道会在最后一个确认收到后关闭。
如果只使用消息返回，通道会保持5s的开放。如果观察到channel快速的关闭和打开，需要考虑将channelCacheSize调整大。

### 消息集成
RabbitMessagingTemplate构建于RabbitTemplate之上，提供和 spring-framework 消息抽象框架的集成。因此可以使用spring-message来
接收和发送消息。spring-message还用于spring的其他框架里。两个消息转换器提供以上功能的支持，一个是负责spring-message和spring-amqp
之间的消息转换，另一个是负责spring-amqp和spring-rabbitmq的消息转换。也可以自定义rabbtitmq-client的消息转换器。

**自定义RabbitTemplate的payload消息转换器**

```
MessagingMessageConverter amqpMessageConverter = new MessagingMessageConverter();
amqpMessageConverter.setPayloadConverter(myPayloadConverter);
rabbitMessagingTempalte.setAmqpMessageConverter(amqpMessageConverter);
```

### 验证userid
从1.6开始，template支持userIdExpression属性，当表达式解析完毕后，在消息被发送时，userid属性就会设置到消息体中。根对象就是要
发送的消息对象。

```
<rabbit:template ... user-id-expression="@myConnectionFactory.username" />
```
