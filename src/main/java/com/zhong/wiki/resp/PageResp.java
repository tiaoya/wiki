package com.zhong.wiki.resp;

import java.util.List;

/**
 * @author : zhong
 * @Description :
 * @create : 2022/9/6-22:45
 */
public class PageResp<T> {
    private long total;
    private List<T> list;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageResp{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}
