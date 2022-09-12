package com.zhong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhong.wiki.domain.Content;
import com.zhong.wiki.domain.Doc;
import com.zhong.wiki.domain.DocExample;
import com.zhong.wiki.mapper.ContentMapper;
import com.zhong.wiki.mapper.DocMapper;
import com.zhong.wiki.req.DocQueryReq;
import com.zhong.wiki.req.DocSaveReq;
import com.zhong.wiki.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(){
        DocExample docExample = new DocExample();
        // 设置排序
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);
        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }


    public PageResp<DocQueryResp> list(DocQueryReq req){

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        // 创建查询条件
        DocExample.Criteria criteria = docExample.createCriteria();

        // 查询第几页,每页几条
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数: {}",pageInfo.getTotal());
        LOG.info("总页数: {}",pageInfo.getPages());


//        List<DocResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc,docResp);
//            // 对象复制
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }


        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
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
   public void save (DocSaveReq req){
       // CopyUtil 可以将请求参数变成实体
       Doc doc = CopyUtil.copy(req, Doc.class);
       Content content = CopyUtil.copy(req, Content.class);
       if (ObjectUtils.isEmpty(req.getId())){
           // 新增, 根据雪花算法生成下一个id
           doc.setId(snowFlake.nextId());
           docMapper.insert(doc);

           // 保存富文本内容 , 向数据库插入内容
           content.setId(doc.getId());
           contentMapper.insert(content);
       }else {
           // 更新
           docMapper.updateByPrimaryKey(doc);

           // 更新富文本内容  BLOB代表富文本字段,这是一个大字段
           int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
           // 要么更新，如果没有就插入
           if (count == 0){
               contentMapper.insert(content);
           }
       }
   }

   /**
   * @param :
   * @description : 删除
   */
   public void delete(Long id){
       docMapper.deleteByPrimaryKey(id);
   }

   /**
   * @param :
   * @description : 增加删除文档功能
   */
   public void delete(List<String> ids){
       // 根据条件查询
       DocExample docExample = new DocExample();
       DocExample.Criteria criteria = docExample.createCriteria();
       criteria.andIdIn(ids);
       docMapper.deleteByExample(docExample);

//       docMapper.deleteByPrimaryKey(id);
   }

   /**
   * @param :
   * @description : 根据id查询内容
   */
   public String findContent(Long id){
       Content content = contentMapper.selectByPrimaryKey(id);
       if (content != null){
           return content.getContent();
       }
        return null;
   }




}

