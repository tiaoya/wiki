package com.zhong.wiki.service;

import com.zhong.wiki.domain.Test;
import com.zhong.wiki.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/2-20:18
 */
@Service
public class TestService {

    @Autowired
    private TestMapper testMapper;

    public List<Test> list(){
        return testMapper.list();
    }


}
