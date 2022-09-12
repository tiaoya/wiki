package com.zhong.wiki.controller;

import com.zhong.wiki.req.DocQueryReq;
import com.zhong.wiki.req.DocSaveReq;
import com.zhong.wiki.resp.DocQueryResp;
import com.zhong.wiki.resp.CommonResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocService docService;


    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;

    }


    /**
    * @param :
    * @description : 查询所有电子书数据
    */
    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp();
        PageResp<DocQueryResp> list =  docService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
    * @param :
    * @description : 保存编辑
     *
     * @RequestBody 这个注解对应的就是json方式的(POST)提交，就像我们写这个doc，
     * 这个是用这个content-type是 application/json
    */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp();
        docService.save(req);
        return resp;
    }

    /**
    * @param :
    * @description : 删除
    */
    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }

    /**
    * @param :
    * @description : 获取文本内容
    */
    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

}
