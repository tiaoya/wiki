package com.zhong.wiki.controller;

import com.zhong.wiki.req.CategoryQueryReq;
import com.zhong.wiki.req.CategorySaveReq;
import com.zhong.wiki.resp.CommonResp;
import com.zhong.wiki.resp.CategoryQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/1-16:46
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        resp.setContent(list);
        return resp;

    }


    /**
    * @param :
    * @description : 查询所有电子书数据
    */
    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp();
        PageResp<CategoryQueryResp> list =  categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
    * @param :
    * @description : 保存编辑
     *
     * @RequestBody 这个注解对应的就是json方式的(POST)提交，就像我们写这个category，
     * 这个是用这个content-type是 application/json
    */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp();
        categoryService.save(req);
        return resp;
    }

    /**
    * @param :
    * @description : 删除
    */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp();
        categoryService.delete(id);
        return resp;
    }

}
