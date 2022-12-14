package com.zhong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhong.wiki.config.WebSocketConfig;
import com.zhong.wiki.domain.Content;
import com.zhong.wiki.domain.Doc;
import com.zhong.wiki.domain.DocExample;
import com.zhong.wiki.exception.BusinessException;
import com.zhong.wiki.exception.BusinessExceptionCode;
import com.zhong.wiki.mapper.ContentMapper;
import com.zhong.wiki.mapper.DocMapper;
import com.zhong.wiki.mapper.DocMapperCust;
import com.zhong.wiki.req.DocQueryReq;
import com.zhong.wiki.req.DocSaveReq;
import com.zhong.wiki.resp.DocQueryResp;
import com.zhong.wiki.resp.PageResp;
import com.zhong.wiki.util.CopyUtil;
import com.zhong.wiki.util.RedisUtil;
import com.zhong.wiki.util.RequestContext;
import com.zhong.wiki.util.SnowFlake;
import com.zhong.wiki.websocket.WebSocketServer;
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
    private DocMapperCust docMapperCust;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public WebSocketServer webSocketServer;


    public List<DocQueryResp> all(Long ebookId){
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
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
           // 设置为0 当点击的时候阅读数才会增加, 如果数据库里的是null 那么点击的时候不会增加
           doc.setViewCount(0);
           doc.setVoteCount(0);
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
       // 文档阅读数 +1
       docMapperCust.increaseViewCount(id);
       // 非空判断
       if (ObjectUtils.isEmpty(content)){
           return "";
       }else {
           return content.getContent();
       }
   }

   /**
   * @param :
   * @description : 点赞
   */
   public void vote(Long id){
//       docMapperCust.increaseVoteCount(id);
       // 远程IP+doc.id 作为key,24 小时内不能重复 点赞
       String ip = RequestContext.getRemoteAddr();
       if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 5000)) {
           docMapperCust.increaseVoteCount(id);
       } else {
           throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
       }

       // 推送消息
       Doc docDb = docMapper.selectByPrimaryKey(id);
       webSocketServer.sendInfo("【"+ docDb.getName()+"】被点赞了!");
   }

   // 按电子书分组统计文档数据, 并更新到对应的电子书中
    public void updateEbookInfo(){
       docMapperCust.updateEbookInfo();
    }


}

