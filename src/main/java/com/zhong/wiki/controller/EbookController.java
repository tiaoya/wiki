package com.zhong.wiki.controller;

import com.zhong.wiki.domain.Ebook;
import com.zhong.wiki.req.EbookReq;
import com.zhong.wiki.resp.CommonResp;
import com.zhong.wiki.resp.EbookResp;
import com.zhong.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

   

    @GetMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<List<EbookResp>> resp = new CommonResp();
        List<EbookResp> list =  ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

}
