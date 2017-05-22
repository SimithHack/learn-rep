package stream;

import com.xie.learn.springcloud.configclient.controller.stream.StreamApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by xfq on 17/4/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StreamApplication.class)
@WebAppConfiguration
@DirtiesContext
public class StreamApplicationTests {
    @Autowired
    private Sink sink;

    @Test
    public void contextLoads(){
        SubscribableChannel channel = this.sink.input();
        Assert.assertNotNull(channel);
    }
}
