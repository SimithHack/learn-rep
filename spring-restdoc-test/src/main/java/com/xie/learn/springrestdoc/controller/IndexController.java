package com.xie.learn.springrestdoc.controller;

import com.xie.learn.springrestdoc.dto.Book;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xfq on 17/1/2.
 * RESTful接口
 */
@RestController
public class IndexController {
    /**
     * 首页请求
     * @return
     */
    @RequestMapping("/")
    public Book index(){
        Book book = new Book();
        book.setId(1l);
        book.setTitle("我的第一本书");
        return book;
    }
    @RequestMapping(value = "/books",method = RequestMethod.GET)
    public List<Book> books(){
        List<Book> books = new ArrayList();
        long begin=100l;
        for(int i=0;i<10;i++){
            Book book = new Book();
            book.setId(i+begin);
            book.setTitle("书本" + i);
            books.add(book);
        }
        return books;
    }
}
