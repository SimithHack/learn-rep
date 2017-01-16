package com.xie.learn.springrestdoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

/**
 * Created by xfq on 17/1/2.
 * 使用非junit环境，使用说明参见StartJunitTest，比如TestNG
 */
public class StartNoneJunitTest {
    /**
     * 没有@Rule
     */
    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation("target/generated-snippets");
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    /**
     * 必须在任何测试方法执行之前，执行初始化
     *  注解需要引用TestNG包
     */
    //@BeforeMethod
    public void setUp(Method method){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
                .build();
        this.restDocumentation.beforeTest(getClass(),method.getName());
    }
    /**
     * 最后一步需要在每个测试完成之后调用
     *  注解需要引用TestNG包
     */
    //@AfterMethod
    public void tearDown(){
        this.restDocumentation.afterTest();
    }

}
