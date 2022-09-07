package com.zhong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhong.wiki.domain.Ebook;
import com.zhong.wiki.domain.EbookExample;
import com.zhong.wiki.mapper.EbookMapper;
import com.zhong.wiki.req.EbookQueryReq;
import com.zhong.wiki.req.EbookSaveReq;
import com.zhong.wiki.resp.EbookQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.util.CopyUtil;
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
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Autowired
    private EbookMapper ebookMapper;

    public PageResp<EbookQueryResp> list(EbookQueryReq req){

        EbookExample ebookExample = new EbookExample();
        // 创建查询条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        // 模糊查询
        if (!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+ req.getName() +"%");
        }
        // 查询第几页,每页几条
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数: {}",pageInfo.getTotal());
        LOG.info("总页数: {}",pageInfo.getPages());


//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook,ebookResp);
//            // 对象复制
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }


        // 列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
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
   public void save (EbookSaveReq req){
       // CopyUtil 可以将请求参数变成实体
       Ebook ebook = CopyUtil.copy(req, Ebook.class);
       if (ObjectUtils.isEmpty(req.getId())){
           // 新增
           ebookMapper.insert(ebook);
       }else {
           // 更新
           ebookMapper.updateByPrimaryKey(ebook);
       }
   }




}

