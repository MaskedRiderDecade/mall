package com.tmall.mall.service;

import com.github.pagehelper.PageInfo;
import com.tmall.mall.vo.OrderVo;
import com.tmall.mall.vo.ResponseVo;

public interface IOrderService {
    ResponseVo<OrderVo>create(Integer uid,Integer shippingId);
    ResponseVo<OrderVo>detail(Integer uid,Long orderNo);
    ResponseVo<PageInfo>list(Integer uid, Integer pageNum,Integer pageSize);
    ResponseVo cancel(Integer uid,Long orderNo);
    void paid(Long orderNo);
}
