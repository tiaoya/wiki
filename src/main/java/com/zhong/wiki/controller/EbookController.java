package com.zhong.wiki.controller;

import com.zhong.wiki.req.EbookQueryReq;
import com.zhong.wiki.req.EbookSaveReq;
import com.zhong.wiki.resp.CommonResp;
import com.zhong.wiki.resp.EbookQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;


    /**
    * @param :
    * @description : 查询所有电子书数据
    */
    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp();
        PageResp<EbookQueryResp> list =  ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
    * @param :
    * @description : 保存编辑
     *
     * @RequestBody 这个注解对应的就是json方式的(POST)提交，就像我们写这个ebook，
     * 这个是用这个content-type是 application/json
    */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp();
        ebookService.save(req);
        return resp;
    }

    /**
    * @param :
    * @description : 删除
    */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp();
        ebookService.delete(id);
        return resp;
    }

}
