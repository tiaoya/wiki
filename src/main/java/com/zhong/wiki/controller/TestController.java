package com.zhong.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello(){
        return "hello world!";
    }


    @PostMapping("/hello/post")
    public String helloPort(String name){
        return "Hello World! Post," +name;
    }
}
