package com.zhong.wiki.service;

import com.zhong.wiki.domain.Ebook;
import com.zhong.wiki.domain.EbookExample;
import com.zhong.wiki.mapper.EbookMapper;
import com.zhong.wiki.req.EbookReq;
import com.zhong.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/2-20:18
 */
@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){

        EbookExample ebookExample = new EbookExample();
        // 创建查询条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        // 模糊查询
        criteria.andNameLike("%"+ req.getName() +"%");

        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> respList = new ArrayList<>();

        for (Ebook ebook : ebookList) {
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook,ebookResp);
            respList.add(ebookResp);
        }

        return respList;

    }


}
