package com.zhong.wiki.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryQueryReq extends PageReq{

    @Override
    public String toString() {
        return "CategoryQueryReq{} " + super.toString();
    }
}