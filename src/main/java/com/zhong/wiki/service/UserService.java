package com.zhong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhong.wiki.domain.User;
import com.zhong.wiki.domain.UserExample;
import com.zhong.wiki.mapper.UserMapper;
import com.zhong.wiki.req.UserQueryReq;
import com.zhong.wiki.req.UserSaveReq;
import com.zhong.wiki.resp.UserQueryResp;
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
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;


    public PageResp<UserQueryResp> list(UserQueryReq req){

        UserExample userExample = new UserExample();
        // 创建查询条件
        UserExample.Criteria criteria = userExample.createCriteria();

        if (!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andLoginNameEqualTo(req.getLoginName());
        }


        // 查询第几页,每页几条
        PageHelper.startPage(req.getPage(),req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数: {}",pageInfo.getTotal());
        LOG.info("总页数: {}",pageInfo.getPages());


//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user,userResp);
//            // 对象复制
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }


        // 列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
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
   public void save (UserSaveReq req){
       // CopyUtil 可以将请求参数变成实体
       User user = CopyUtil.copy(req, User.class);
       if (ObjectUtils.isEmpty(req.getId())){
           // 新增, 根据雪花算法生成下一个id
           user.setId(snowFlake.nextId());
           userMapper.insert(user);
       }else {
           // 更新
           userMapper.updateByPrimaryKeySelective(user);

       }
   }

   /**
   * @param :
   * @description : 删除
   */
   public void delete(Long id){
       userMapper.deleteByPrimaryKey(id);
   }



}

