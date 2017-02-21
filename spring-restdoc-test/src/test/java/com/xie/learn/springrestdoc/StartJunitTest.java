package com.xie.learn.springrestdoc;

import com.xie.learn.springrestdoc.controller.Boot;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by xfq on 17/1/2.
 * 使用spring-rest-doc结合junit来测试
 */
@RunWith(SpringRunner.class)
//指定Spring-Boot启动程序
@SpringBootTest(classes = Boot.class)
public class StartJunitTest {
    /**
     * 第一步: 设置junit环境
     *  定义输出路径，maven是target/generated-snippets
     *  @Rule是Junit提供，定义在public方法上（返回值是TestRule类型）或者public属性域上（TestRule)不可是静态方法或者属性
     *  执行流程是运行传入的statement中的所有before方法--> test方法 --> after方法
     *  Rules defined by fields will always be applied before Rules defined by methods
     */
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    /**
     * 第二步: 配置MockMvc或者Rest Assured
     */
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Before
    public void setUp(){
        this.mockMvc = webAppContextSetup(this.context)
                //MocMvc使用MockMvcRestDocumentationConfigurer来配置实例
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }
    /**
     * 第三步: 调用RESTful服务
     */
    @Test
    public void testExecution() throws Exception{
        //调用"/"服务，并期待application/json格式的数据
        this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //将片段写入"index"目录
                .andDo(document("book/1",responseFields
                    (
                        fieldWithPath("id").description("主键"),
                        fieldWithPath("title").description("书名")
                    )));
    }

    /**
     * 重用描述
     * @throws Exception
     */
    @Test
    public void testArrayBooks() throws Exception {
        FieldDescriptor[] book = new FieldDescriptor[]{
            fieldWithPath("id").description("主键"),
            fieldWithPath("title").description("书名")
        };
        this.mockMvc.perform(get("/books").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("book",responseFields(
                        fieldWithPath("[]").description("图书馆-计算机书")
                ).andWithPrefix("[].",book)));
    }
}
