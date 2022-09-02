package com.zhong.wiki.controller;

import com.zhong.wiki.domain.Test;
import com.zhong.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/hello")
    public String hello(){
        return "hello world!";
    }


    @PostMapping("/hello/post")
    public String helloPort(String name){
        return "Hello World! Post," +name;
    }


    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }

}
