
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
