## 消息发送
可以使用下边三个函数发送消息
```
void send(Message message) throws AmqpException;
void send(String routingKey, Message message) throws AmqpException;
void send(String exchange, String routingKey, Message message) throws AmqpException;
```
**例如**

```
amqpTemplate.send("marketData.topic", "quotes.nasdaq.FOO",
      new Message("12.34".getBytes(), someProperties));
```
倘若想使用template直接发送消息，那么exchange需要配置到template上，此时可以使用第二个方法。这
对于需要重复向同一个exchange发送消息的时候非常方便。

```
amqpTemplate.setExchange("marketData.topic");
amqpTemplate.send("quotes.nasdaq.FOO", new Message("12.34".getBytes(), someProperties));
```

当让，exchange和routing-key都可以配置到template，从而减轻更多重复的工作。

```
amqpTemplate.setExchange("marketData.topic");
amqpTemplate.setRoutingKey("quotes.nasdaq.FOO");
amqpTemplate.send(new Message("12.34".getBytes(), someProperties));
```

**规则**

>越更多具体参数的方法，越能够覆盖默认配置，即使不适用template的set方法配置exchange和routingKey
也会有默认的值。默认的exchange是名字为空字符串的exchange，这在amqp协议里是合法的，并且，这个exchange也是
amqp里规定的默认exchange，它是direct类型的exchange，而且所有的queue都会自动的绑定到这个exchange上。
使用queue的name作为routing-key.

### 消息构建API
从1.3开始，提供MessageBuilder和MessagePropertiesBuilder流式方式构建消息实例。

```
//构建属性
MessageProperties props = MessagePropertiesBuilder.newInstance()
    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
    .setMessageId("123")
    .setHeader("bar", "baz")
    .build();
//构建消息
Message message = MessageBuilder.withBody("foo".getBytes())
    .andProperties(props)
    .build();
```
* MessageProperties里的所有属性都可以设置，setHeader,removeHeader,removeHeaders和copyProperties(MessageProperties)等方法
每一个属性的设置方法都有一个set*IfAbsent()和set*IfAbsentOrDefault()变种，用来处理默认值的情况。
* 其他静态方法。
```
//新创建的消息实例，直接引用body
public static MessageBuilder withBody(byte[] body)
//消息实例，重建一个body副本
public static MessageBuilder withClonedBody(byte[] body)
//body的子副本
public static MessageBuilder withBody(byte[] body, int from, int to)
//新创建的消息实例直接引用Message的MessageProperties
public static MessageBuilder fromMessage(Message message)
//message的副本MessageProperties对象
public static MessageBuilder fromClonedMessage(Message message)
public static MessagePropertiesBuilder newInstance()
public static MessagePropertiesBuilder fromProperties(MessageProperties properties)
public static MessagePropertiesBuilder fromClonedProperties(MessageProperties properties)
```
* 对于AmqpTemplate的实现RabbitTemplate来说，还有一个send()重载方法，可接受CorrelationData对象，此对象在当
消息发送者开启了消息确认模式时允许发送者在发送消息时候给消息关联一个确认信息(ack|nack)。这个信息会在回调里返回给
消息发送者。

### 批处理
BatchingRabbitTemplate是RabbitTemplate的子类，覆盖了send方法，send方法里根据BatchingStrategy来批量处理消息
，仅当消息发送到RabbitMQ后批处理才算完成。

* 带处理的消息保存在内存里，任何系统问题都将导致消息丢失。

**策略接口**

```
public interface BatchingStrategy {

	MessageBatch addToBatch(String exchange, String routingKey, Message message);

	Date nextRelease();

	Collection<MessageBatch> releaseBatches();

}
```
* SimpleBatchStrategy实现支持将所有的消息发送到同的一个exchange/routingKey里。它有如下属性
>batchSize 在消息发送之前，消息的数量
>bufferLimit 批量处理消息的上限
>timeout 部分消息已经发送等待新的消息加入的间隔时间。

* SimpleBatchingStrategy会将每个Message使用4个字节的长度隔开，并向消息接受者发送springBatchFormat消息属性
其值为lengthHeader4.
>批量消息会自动在接收方分解成单独的Message(使用springBatchFormat属性)。任何消息的驳回都将导致整个批量
消息请求被拒绝。
