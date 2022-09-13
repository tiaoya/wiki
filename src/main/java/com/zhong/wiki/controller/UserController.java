package com.zhong.wiki.controller;

import com.zhong.wiki.req.UserQueryReq;
import com.zhong.wiki.req.UserSaveReq;
import com.zhong.wiki.resp.CommonResp;
import com.zhong.wiki.resp.UserQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
    * @param :
    * @description : 查询所有电子书数据
    */
    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp();
        PageResp<UserQueryResp> list =  userService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
    * @param :
    * @description : 保存编辑
     *
     * @RequestBody 这个注解对应的就是json方式的(POST)提交，就像我们写这个user，
     * 这个是用这个content-type是 application/json
    */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        CommonResp resp = new CommonResp();
        userService.save(req);
        return resp;
    }

    /**
    * @param :
    * @description : 删除
    */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp();
        userService.delete(id);
        return resp;
    }

}
