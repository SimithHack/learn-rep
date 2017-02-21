
## Connection 连接
RabbitMQ 管理连接的核心接口是ConnectionFactory，它主要用来提供Connection实例。目前唯一实现为
CachingConnectionFactory，它只为整个应用程序建立一个单独的Connection代理。连接的分时工作得益于AMQP
本身的“channel”方式的消息通信。就好比在JMS中的connection和session的关系。Connection实例提供一个
createChannel的方法。CachingConnectionFactory可以把创建出来的channel缓存起来，并且按照chanel是否
是事务的熟悉来分别创建缓存区。
* 创建CachingConnectionFactory的时候可以通过构造方法制定hostname,username,pasword
* 可以调用setChannelCacheSize设置缓存channel的大小。默认为25个。
* 从1.3开始CachingConnectionFactory还可以缓存connection了，因此每次调用createConnection()方法要么创建新的，要么返回空闲的。
并且这些缓存的connection创建的channel也是缓存的。**需要将cacheMode设置为CacheMode.CONNECTION**
* 从1.5.5开始，新的connectionLimit属性用来限制缓存中允许的 **空闲connection数量**(不是限制connection连接数)

**注意**
>1 当缓存模式（cacheMode)为CONNECTION时，自动创建queue的功能就不支持了，在写这篇文章时rabbitmq-client会默认为每一个connection创建
固定大小（5）的线程池，这对于需要高并发的应用来说是不够的，因此需要在CachingConnectionFactory设置自定义的Executor。但是也要注意线程池
应当是unbounded. 不然会影响并发性能。

>2 注意cache size，如果通过RabbitMQ 的Admin UI观察到大量的channel频繁的关闭，需要注意增加cache size值。

>3 从1.4.2开始CachingConnectionFactory有一个属性channelCheckoutTimeout如果配置为0，则意味着channelCacheSize
就变成能够创建的channel的数量了，超过这个数量，就会阻塞。

* 使用RabbitTemplate创建的channel会自动返回给cache. 不使用RabbitTemplate创建的channel需要自己维护。

```
CachingConnectionFactory connectionFactory = new CachingConnectionFactory("somehost");
connectionFactory.setUsername("guest");
connectionFactory.setPassword("guest");
Connection connection = connectionFactory.createConnection();
```
* SingleConnectionFactory 实现只用在测试中，没有缓存，不建议用在生产环境中。
* 如果想实现自己的ConnectionFactory，可以从AbstractConnectionFactory继承。
* address属性可以配置多个rabbit broker（聚簇）

```
<rabbit:connection-factory id="connectionFactory" addresses="host1:5672,host2:5672"/>
```
* 可为rabbitMQ创建的连接指定和应用相关的前缀，方便用户识别哪些连接是自己应用产生的。默认使用beanName和内部计数器产生connectName

```
connectionFactory.setConnectionNameStrategy(connectionFactory -> "MY_CONNECTION");
```

### 配置客户端连接工厂
CachingConnectionFactory使用Rabbit client的ConnectionFactory，并把相关的配置属性传递给ConnectionFactory，如果想要直接引用ConnectionFactory来配置其他的信息。可以通过CachingConnectionFactory获取rabbit-client的ConnectionFactory。
```
<rabbit:connection-factory
      id="connectionFactory" connection-factory="rabbitConnectionFactory"/>
```

### 配置SSL
从1.4开始，提供了一个RabbitConnectionFactoryBean方便配置SSL
```
<rabbit:connection-factory id="rabbitConnectionFactory"
    connection-factory="clientConnectionFactory"
    host="${host}"
    port="${port}"
    virtual-host="${vhost}"
    username="${username}" password="${password}" />

<bean id="clientConnectionFactory"
        class="org.springframework.xd.dirt.integration.rabbit.RabbitConnectionFactoryBean">
    <property name="useSSL" value="true" />
    <property name="sslPropertiesLocation" value="file:/secrets/rabbitSSL.properties"/>
</bean>
```
* 省略了keyStore和trustStore的配置，它们通过sslPropertiesLocation指定的资源文件配置

```
keyStore=file:/secret/keycert.p12
trustStore=file:/secret/trustStore
keyStore.passPhrase=secret
trustStore.passPhrase=secret
```
此资源文件由操作系统保护，确保应用程序具有 **read** 权限。

### 路由连接工厂
从1.3开始，AbstractRoutingConnectionFactory 提供了映射多个ConnectionFactory的机制。它可以在运行时刻通过查找lookupKey来
决定使用哪一个ConnectionFactory。SpringMQ提供了SimpleRoutingConnectionFactory实现类，它从SimpleResourceHolder中获取lookupKey
```
<bean id="connectionFactory"
      class="org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory">
	<property name="targetConnectionFactories">
		<map>
			<entry key="#{connectionFactory1.virtualHost}" ref="connectionFactory1"/>
			<entry key="#{connectionFactory2.virtualHost}" ref="connectionFactory2"/>
		</map>
	</property>
</bean>
```

java代码如下
```
public class MyService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void service(String vHost, String payload) {
        SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), vHost);
        rabbitTemplate.convertAndSend(payload);
        //记住需要在使用之后解绑
        SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
    }

}
```
从1.4开始，RabbitTemplate还支持SpELl表达式。sendConnectionFactorySelectorExpression和receiveConnectionFactorySelectorExpression

**路由算法**
>1 如果选择器表达式为null，或者提供的ConnectionFactory并不是AbstractRoutingConnectionFactory的子类，那么一切如平常一样什么事也没有，仅仅依赖于所提供的ConnectionFactory实现逻辑。

>2 如2果表达式不为null，但是没有lookupKey相匹配的ConnectionFactory，并且此时AbstractRoutingConnectionFactory的lenientFallback配置为false，那么情况如 **1**。

>3 否则（1，2都不满足）则有determineCurrentLookupKey()方法决定路由逻辑。但是如果此时lenientFallback=false，抛出IllegalStateException异常。

### LocalizedQueueConnectionFactory 优先本地连接仓库
处于性能考虑，最好连接到master queue所在的物理消息代理机上。但是CachingConnectionFactory可以配置多个地址，客户端按顺序尝试连接。
LocalizedQueueConnectionFactory使用管理插件提供的API决定master queue所在的物理机。然后创建一个CachingConnectionFactory连接
那台物理机。LocalizedQueueConnectionFactory会默认配置默认的CachingConnectionFactory以防止master queue的物理机无法找到。
* 每个节点需要启用管理插件
* 只对那些配置单个queue的容器有效
* LocalizedQueueConnectionFactory是一个RoutingConnectionFactory

```
@Autowired
private RabbitProperties props;

private final String[] adminUris = { "http://host1:15672", "http://host2:15672" };

private final String[] nodes = { "rabbit@host1", "rabbit@host2" };

@Bean
public ConnectionFactory defaultConnectionFactory() {
    CachingConnectionFactory cf = new CachingConnectionFactory();
    cf.setAddresses(this.props.getAddresses());
    cf.setUsername(this.props.getUsername());
    cf.setPassword(this.props.getPassword());
    cf.setVirtualHost(this.props.getVirtualHost());
    return cf;
}

@Bean
public ConnectionFactory queueAffinityCF(
        @Qualifier("defaultConnectionFactory") ConnectionFactory defaultCF) {
    //注意，addresses,adminUris和nodes必须是一一对应的，因为数组相同位置共同决定着一台节点的连接地址
    return new LocalizedQueueConnectionFactory(defaultCF,
            StringUtils.commaDelimitedListToStringArray(this.props.getAddresses()),
            this.adminUris, this.nodes,
            this.props.getVirtualHost(), this.props.getUsername(), this.props.getPassword(),
            false, null);
}
```

### 消息发布方确认和返回
设置CachingConnectionFactory的publsherConfirms和publisherReturns属性为true可以开启消息的确认和返回支持。
当这两个属性配置后，被工厂创建的Channel会包裹在一个PublisherCallbackChannel里边，客户端可以向此channel注册
PublisherCallbackChannel.Listener。PublisherCallbackChannel自己实现了将确认和返回消息路由到恰当的监听器的路基。

### Connection 和 Channel监听器
ConnectionFactory支持ConnectionListener和ChannelListener，这可以让它在获取有关连接和channel方面的事件通知。

```
@FunctionalInterface
public interface ConnectionListener {
    void onCreate(Connection connection);
    default void onClose(Connection connection) {}
    default void onShutDown(ShutdownSignalException signal) {}
}
@FunctionalInterface
public interface ChannelListener {
    void onCreate(Channel channel, boolean transactional);
    default void onShutDown(ShutdownSignalException signal) {}
}
```

### 日志记录
CachingConnectionFactory使用默认的策略记录channel闭包的日志

* 正常关闭的channel不记录日志
* 如果channel的关闭是因为queue的声明失败产生的，日志以debug级别记录。
* 如果channel的关闭是因为 basic.consume 命令因为互斥的consume条件被拒绝，日志以INFO级别记录。
* 所有其他的channel关闭都记录ERROR日志级别。

### 实时缓存配置属性
从1.6开始，CachingConnectionFactory提供了getCacheProperties()方法用来在生产环境里优化缓存。

**属性列表**

CacheMode.CHANNEL

| 属性|含义|
| :--| :--|
| channelCacheSize|允许可空闲的最大channel数|
| localPort|连接的本地端口|
|idleChannelsTx|支持事务的channel空闲数量|
|idleChannelsNotTx|不支持事务的最大channel空闲数量|
|idleChannelsTxHighWater|事务channel允许同时空闲的最大数量|
|idleChannelsNotTxHighWater|非事务channel允许同时空闲的最大数量|

CacheMode.CONNECTION

|属性|含义|
|:-|:-|
|openConnections|连接到broker的connection对象的数量|
|channelCacheSize|当前配置锁允许的最大空闲channel数量|
|connectionCacheSize|当前配置所允许的最大空闲connection数量|
|idleConnections|当前空闲的连接数|
|idleConnectionHighWater|共存的空闲connection最大数|
|idleChannelsTx:<localhost>|本次Connection下支持事务的空闲channel数|
|idleChannelsNotTx:<localPort>|...|
|idleChannelsTxHighWater|..concurrently...|
|idleChannelsNotTxHighWater|...concurrently...|

### RabbitMQ自动连接和拓扑恢复
spring AMQP从一开始就支持connection和channel在broker发生异常时自动恢复的机制。当时rabbitmq-client 还不支持这个功能
但是从4.0.x，rabbitmq-client自动恢复功能默认是开启的。所以可通过将connectionFactory的automaticRecoveryEnabled配置
为false从而使用client的自动恢复机制。但是如果要兼容，还是建议不关闭此功能。

### 添加自定义客户端连接属性
```
connectionFactory.getRabbitConnectionFactory().getClientProperties().put("foo", "bar");
```
