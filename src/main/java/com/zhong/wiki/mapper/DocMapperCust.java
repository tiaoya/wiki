package com.zhong.wiki.mapper;

import com.zhong.wiki.domain.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/2-20:13
 */
public interface DocMapperCust {
    // 增加文档阅读数
    void increaseViewCount(@Param("id") Long id);
    // 增加文档点赞数
    void increaseVoteCount(@Param("id") Long id);
    // 统计电子数, 按电子书分组统计文档数据, 并更新到对应的电子书中
    void updateEbookInfo();
}
