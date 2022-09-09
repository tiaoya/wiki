package com.zhong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhong.wiki.domain.Category;
import com.zhong.wiki.domain.CategoryExample;
import com.zhong.wiki.mapper.CategoryMapper;
import com.zhong.wiki.req.CategoryQueryReq;
import com.zhong.wiki.req.CategorySaveReq;
import com.zhong.wiki.resp.CategoryQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.util.CopyUtil;
import com.zhong.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/2-20:18
 */
@Service
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;


    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){

        CategoryExample categoryExample = new CategoryExample();
        // 创建查询条件
        CategoryExample.Criteria criteria = categoryExample.createCriteria();

        // 查询第几页,每页几条
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数: {}",pageInfo.getTotal());
        LOG.info("总页数: {}",pageInfo.getPages());


//        List<CategoryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
////            CategoryResp categoryResp = new CategoryResp();
////            BeanUtils.copyProperties(category,categoryResp);
//            // 对象复制
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
//            respList.add(categoryResp);
//        }


        // 列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);


        return pageResp;
    }


   /**
   * @param :
   * @description : 保存
    * 保存分新增保存和编辑保存，这里是编辑保存
    *
    * 判断是新增还是更新 一般是根据 request 这个参数里面的id有没有值，id 右值是更新，id没有值是新增
   */
   public void save (CategorySaveReq req){
       // CopyUtil 可以将请求参数变成实体
       Category category = CopyUtil.copy(req, Category.class);
       if (ObjectUtils.isEmpty(req.getId())){
           // 新增, 根据雪花算法生成下一个id
           category.setId(snowFlake.nextId());
           categoryMapper.insert(category);
       }else {
           // 更新
           categoryMapper.updateByPrimaryKey(category);
       }
   }

   /**
   * @param :
   * @description : 删除
   */
   public void delete(Long id){
       categoryMapper.deleteByPrimaryKey(id);
   }



}

