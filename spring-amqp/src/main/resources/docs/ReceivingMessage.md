## 接收消息
比消息发送复杂些，两种方式接收消息。
* 每次只获取一条消息。
* 注册监听器，异步获取消息。

### Polling 消费端
AmqpTemplate 本身可以用于消息接收。如果没有任何可用的消息，立即返回NULL
，方法调用不会阻塞。从1.5后，可以设置receiveTimeout来让其等待消息返回一段时间
。如果此值设置为小于0的值，表示无限等待，直到消息返回或者连接丢失。
* 框架提供了四种receive方法，参数少的就要求template提供，参数多的可以覆盖template提供的属性配置。
* 框架方便的方法用于将Message直接转化为POJO。当然AmqpTemplate的实现提供了自定义MessageConvert的入口。
```
Object receiveAndConvert() throws AmqpException;
Object receiveAndConvert(String queueName) throws AmqpException;
Message receiveAndConvert(long timeoutMillis) throws AmqpException;
Message receiveAndConvert(String queueName, long timeoutMillis) throws AmqpException;
```
* 从2.0开始，还提供了receiveAndConvert的额外重载方法，它可以接受一个ParameterizedTypeReference参数，用以处理
复杂对象。当然此时template必须配置SmartMessageConverter消息转换器。
* 从1.3开始，AmqpTemplate提供接受消息的同步方法。就是那些receiveAndReply方法。
```
boolean received =
        this.template.receiveAndReply(ROUTE, new ReceiveAndReplyCallback<Order, Invoice>() {

                public Invoice handle(Order order) {
                        return processOrder(order);
                }
        });
if (received) {
        log.info("We received an order!");
}
```
>1 AmqpTemplate的实现负责处理接受和响应的细节。用户只需要实现ReceiveAndReplyCallback接口，来
处理字节的业务逻辑，然后返回响应对象或者消息。也可返回null。  
>2 自动消息回话（自动request和reply)只有当callback不是ReceiveAndReplyMessageCallback的实例。  
>3 ReplyToAddressCallback在那些如果需要自定义逻辑去决定replayto的地址的场景很有用。

### 异步消费
spring-amqp支持注解方式的监听节点。**@RabbitListener** 这使得架构更加开放化。

### 消息监听器
一个专门的组件处理异步消息接收。这个组件是消息消费的回调容器。  
首先我们看看回调机制，它是应用代码和消息系统集成的地方。
**MessageListener**  
```
public interface MessageListener {
    void onMessage(Message message);
}
```
如果callback的逻辑依赖于AMQP的Channel接口，我们应该使用下面的接口。  
```
public interface ChannelAwareMessageListener {
    void onMessage(Message message, Channel channel) throws Exception;
}
```

### MessageListenerAdapter
将应用逻辑和messageing API 严格分开。这种技术经常被业界称为 **Message-Driven POJO**  
1.5引入了@RabbitListener更加灵活的机制。

### 容器
容器推动listener运动。容器是一个具有生命周期特性的组件，它提供开启和停止的方法。配置容器时，你
只是从本质上架起了AMQP Queue和MessageListener实例之间的桥梁，你必须提供ConnectionFactory实例和
队列的名称或者队列实例，从而让容器知道listener从哪儿获取消息。  
2.0之前，仅有SimpleMessageListenerContainer一个容器，而现在还有DirectMessageListenerContainer容器。  
**使用SimpleMessageListenerContainer的例子**  
```
SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
container.setConnectionFactory(rabbitConnectionFactory);
container.setQueueNames("some.queue");
container.setMessageListener(new MessageListenerAdapter(somePojo));
```
**容器配置例子**
```
@Configuration
public class ExampleAmqpConfiguration {

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory());
        container.setQueueName("some.queue");
        container.setMessageListener(exampleListener());
        return container;
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory =
            new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public MessageListener exampleListener() {
        return new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("received: " + message);
            }
        };
    }
}
```
* RabbitMQ3.2之后，消息中间件支持消费优先级，可以如下设置。  
```
container.setConsumerArguments(Collections.
<String, Object> singletonMap("x-priority", Integer.valueOf(10)));
```
* 从1.3开始，容器监听的queue可以在运行时刻改变。


### auto-delete Queues
如果容器中使用的queue是auto-delete类型的，那么当容器停止后，queue就会被消息服务器自动删除。那么
下次再启动容器就会报错，从1.3后，容器在启动时自动使用RabbitAdmin重新声明哪些确实的queues.  
```
//声明queue，指明使用containerAdmin初始化
<rabbit:queue id="otherAnon" declared-by="containerAdmin" />
//声明exchange
<rabbit:direct-exchange name="otherExchange" auto-delete="true" declared-by="containerAdmin">
    <rabbit:bindings>
        <rabbit:binding queue="otherAnon" key="otherAnon" />
    </rabbit:bindings>
</rabbit:direct-exchange>
//容器初始化，使用auto-startup=false,表示延迟容器初始化的时间
<rabbit:listener-container id="container2" auto-startup="false">
    <rabbit:listener id="listener2" ref="foo" queues="otherAnon" admin="containerAdmin" />
</rabbit:listener-container>

<rabbit:admin id="containerAdmin" connection-factory="rabbitConnectionFactory"
    auto-startup="false" />
```
* 容器和admin都使用了auto-startup=false参数，表示在ApplicationContext初始化的时候不
创建queue和exchange，直到容器启动时使用containerAdmin来初始化。
