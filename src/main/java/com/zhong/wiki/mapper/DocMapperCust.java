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

    void increaseViewCount(@Param("id") Long id);
}
