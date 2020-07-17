package com.tmall.mall.service;

import com.github.pagehelper.PageInfo;
import com.tmall.mall.vo.ProductDetailVo;
import com.tmall.mall.vo.ResponseVo;

public interface IProductService {
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}
