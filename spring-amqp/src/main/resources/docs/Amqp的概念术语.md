本章主要介绍spring AMQP里最基础的接口和类
## AMQP的概念
spring-amqp包含了许多模块，每个模块都以单独的jar包发布。
* spring-amqp AMQP模式的核心类，提供不依赖于任何AMQP产品的通用抽象，所以对于具体的产品实现是隔离的。
* spring-rabbit 对spring-amqp模块的实现，而且目前也就这一个实现。

## Message 消息
Message类封装了消息体和额外的其他参数，这样让接口调用更加简单。
```
public class Message {
    private final MessageProperties messageProperties;
    private final byte[] body;
    public Message(byte[] body, MessageProperties messageProperties) {
        this.body = body;
        this.messageProperties = messageProperties;
    }
    public byte[] getBody() {
        return this.body;
    }
    public MessageProperties getMessageProperties() {
        return this.messageProperties;
    }
}
```
* MessageProperties定义了一些常用的属性：messageId,timestamp,contentType，可以通过setHeader来扩展用户自定义的属性。

## Exchange 交换机
和AMQP协议里的Exchange一一对应，它是消息生产者发送消息的地方。每一个交换机（Exchange）内部都有一个虚拟主机名称和其他的额外属性。
```
public interface Exchange {
    String getName();
    String getExchangeType();
    boolean isDurable();
    boolean isAutoDelete();
    Map<String, Object> getArguments();
}
```
一个Exchange有一个type字段，它是由ExchangeTypes定义的常量。基础类型有：Direct,Topic,Fanout和Headers，
每一个类型在spring-amqp核心包里都对应一个Exchange借口的实现类。它们的区别只要体现在**queue绑定方式**上。
* AMQP协议规定，每一个消息代理方都需要提供一个 "default" 的exchange，所有声明的queue都会以它们的name作为routing-key绑定这个
默认的exchange上。

## Queue 队列
队列承接着消息消费方接受到的消息。
```
public class Queue  {
    private final String name;
    private volatile boolean durable;
    private volatile boolean exclusive;
    private volatile boolean autoDelete;
    private volatile Map<String, Object> arguments;
    /**
     * The queue is durable, non-exclusive and non auto-delete.
     * @param name the name of the queue.
     */
    public Queue(String name) {
        this(name, true, false, false);
    }
    // Getters and Setters omitted for brevity
}
```

## Binding 消息绑定
连接queue和exchange的桥梁，Binding类代表这些绑定关系。
```
//绑定direct queue
new Binding(someQueue, someDirectExchange, "foo.bar");
//绑定topic exchange
new Binding(someQueue, someTopicExchange, "foo.*");
//绑定fanout exchange不需要routing-key
new Binding(someQueue, someFanoutExchange);
//快捷方式 更清晰自然
Binding b = BindingBuilder.bind(someQueue).to(someTopicExchange).with("foo.*");
```
* Binding只是保存了绑定关系的数据，它并没被激活，它可以被AmqpAdmin激活。
* Binding实例亦可以通过@Bean的方式定义。
* AmqpTemplate是定义在核心包里，作为AMQP消息的主要组件。
