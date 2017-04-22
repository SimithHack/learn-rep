package com.xie.learn.springamqp.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.LocalizedQueueConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xfq on 17/2/28.
 * spring-amqp配置
 */
@Configuration
public class RabbitConfiguration {
    /**
     * rabbitmq的配置属性
     */
    @Autowired
    private RabbitProperties rabbitProperties;
    /**
     * 连接工厂
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        //ConnectionFactory还有一个实现SingleConnectionFactory用在单元测试中，它不缓存channel，如果要实现
        //自己的ConnectionFactory，可以从AbstractConnectionFactory继承
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("172.16.132.145");
        //如果使用的rabbitmq聚簇，需要将brokers的地址配置
        //connectionFactory.setAddresses("host:5672");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        //指定端口
        //connectionFactory.setPort(5672);

        //可配置connection使用的线程工厂类，CustomizableThreadFactory可指定创建的前缀名称
        //connectionFactory.setConnectionThreadFactory(new CustomizableThreadFactory("myprefix-"));

        //设置默认使用的vhost
        //connectionFactory.setVirtualHost("/");

        //设置最大空闲缓存channel数，默认是25
        //connectionFactory.setChannelCacheSize(100);

        //支持缓存使用这种方式创建的channel
        //connectionFactory.createConnection().createChannel();

        //从1.3开始可以配置缓存connection，通过下面方法可以设置,关闭connection后如果空闲缓存大小没有超过下面设置的值，
        //就返回给缓存。同时需要调整缓存模式,只限制放在缓存的数量。而不是能够创建的数量。
        //connectionFactory.setConnectionCacheSize(20);
        //connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);

        //从1.5.5开始，可以设置connectionLimit属性，它将限制允许的connection总数。如果连接总数超过此值，
        //就会等待channelCheckoutTimeLimit所配置的时间，直到超时或者一个空闲connection可用。
        //connectionFactory.setConnectionLimit(10);

        //默认情况下，rabbitmq-client会为每一个连接创建一个具有5个线程数的线程池供使用
        //如果要使用大量的connections,需要自定义Executors，这样所有的connection可以共用一个executor和它的线程池。
        //connectionFactory.setExecutor(Executors.newSingleThreadExecutor());

        //当channelCheckoutTimeout设置的值大于0的话，channelCacheSize就是每个connection能够创建channel最大数量
        //超过此设定，就会阻塞或者设置的时间超时。
        //connectionFactory.setChannelCheckoutTimeout(1000);

        //直接配置rabbitConnectionFactory
        //com.rabbitmq.client.ConnectionFactory rabbitCF = connectionFactory.getRabbitConnectionFactory();

        //使用rabbit-client的ConnectionFactory配置SSL属性,具体配置从略
        //rabbitCF.useSslProtocol();
        //rabbitCF.useSslProtocol(new SSLContext());

        //启用这两个属性后，此工厂创建的channel被包裹成PublisherCallbackChannel，此channel方便使用callback
        //客户端可以向channel注册PublisherCallbackChannel.Listener。PublisherCallbackChannel实现了将confirm/return 路由到
        //正确的监听器里。
        //connectionFactory.setPublisherConfirms(true);
        //connectionFactory.setPublisherReturns(true);

        //以下两个监听器可以监听connection和channel创建，关闭的消息通知。
        /*connectionFactory.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {

            }

            @Override
            public void onClose(Connection connection) {

            }
        });
        connectionFactory.addChannelListener(new ChannelListener() {
            @Override
            public void onCreate(Channel channel, boolean b) {

            }
        });*/

        //缓存统计信息，这些信息可以在运行时刻调整和优化缓存配置
        //Properties cachePropers = connectionFactory.getCacheProperties();

        //设置自定义客户端属性
        connectionFactory.getRabbitConnectionFactory().getClientProperties().put("foo","bar");
        return connectionFactory;
    }

    /**
     * 从1.3开始，可以配置路由connectionFactory
     * 它可以在运行时刻，通过lookupKey决定使用哪一个COnnectionFactory
     * @return
     */
    public ConnectionFactory routingConnectFactory(){
        //从SimpleResourceHolder获取lookupKey
        SimpleRoutingConnectionFactory connectionFactory = new SimpleRoutingConnectionFactory();
        Map<Object,ConnectionFactory> factoryMap = new HashMap();
        ConnectionFactory c1=null;
        ConnectionFactory c2=null;
        ConnectionFactory c3=null;
        factoryMap.put("vhost1",c1);
        factoryMap.put("vhost2",c2);
        factoryMap.put("vhost3",c3);
        connectionFactory.setTargetConnectionFactories(factoryMap);
        return connectionFactory;
    }

    /**
     * connect to the physical broker where the master queue resides
     * 需要管理插件支持，它也是RoutingConnectionFactory，在SimpleMessageListenerContainer使用queue names作为lookupkey
     * @return
     */
    public ConnectionFactory localizedQueueConnectionFactory(){
        LocalizedQueueConnectionFactory connectionFactory = null;//new LocalizedQueueConnectionFactory();
        return connectionFactory;
    }
    /**
     * AmqpAdmin Spring-AMQP抽象
     * @return
     */
    @Bean
    public AmqpAdmin amqpAdmin(){
        return new RabbitAdmin(connectionFactory());
    }

    /**
     * rabbit-mq实现操作工具类
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    /**
     * amqptemplate标准接口工具类
     * @return
     */
    @Bean
    public AmqpTemplate amqpTemplate(){
        /**
         * 为amqptemplate配置重试功能，即在连接失败后，尝试重新连接
         */
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(500);
        policy.setMultiplier(10.0);
        policy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(policy);
        final RabbitTemplate amqpTemplate = rabbitTemplate();
        amqpTemplate.setRetryTemplate(retryTemplate);

        /**
         * 还支持重试回调函数和恢复连接后的回调
         */
        try {
            retryTemplate.execute(
            new RetryCallback<Object, Throwable>() {
                @Override
                public Object doWithRetry(RetryContext retryContext) throws Throwable {
                    //负责，正常的逻辑处理，比如此时我们发送消息
                    retryContext.setAttribute("message", "some info or MessageObject");
                    return null;
                }
            }, new RecoveryCallback<Object>() {
                @Override
                public Object recover(RetryContext retryContext) throws Exception {
                    //负责在连接失败后，重新尝试的逻辑，这里可以使用在上一个回调中房子retryContext里的属性
                    return null;
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //设置事务后，可以在txCommit()方法上检测异常，但是异常显著影响性能，如果只是为了达到检测异常，需要适当考虑
        //因为可以在ConnectionFactory上设置connectionListener来达到同样的目的
        amqpTemplate.setChannelTransacted(true);

        //amqptemplate支持 Publisher Confirms和Returns
        amqpTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {

            }
        });
        amqpTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {

            }
        });
        return amqpTemplate;
    }
}
