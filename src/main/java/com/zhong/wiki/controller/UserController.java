package com.zhong.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhong.wiki.req.UserLoginReq;
import com.zhong.wiki.req.UserQueryReq;
import com.zhong.wiki.req.UserResetPasswordReq;
import com.zhong.wiki.req.UserSaveReq;
import com.zhong.wiki.resp.CommonResp;
import com.zhong.wiki.resp.UserLoginResp;
import com.zhong.wiki.resp.UserQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.service.UserService;
import com.zhong.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
@RequestMapping("/user")
public class UserController {


    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    private RedisTemplate redisTemplate;


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
        // 第二次数据加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

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


    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp();
        userService.resetPassword(req);
        return resp;
    }

   @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp();
        // 我们不需要将密码返回给前端,所有我们的返回实体类中不需要密码
       UserLoginResp userLoginResp = userService.login(req);
       // 这里的token是用雪花算法的id算的，也可以用uuid
       Long token = snowFlake.nextId();
       LOG.info("生成单点登录token：{}，并放入redis中", token);
       userLoginResp.setToken(token.toString()); // 返回给前端的token

       redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

       resp.setContent(userLoginResp);
        return resp;
    }


}
