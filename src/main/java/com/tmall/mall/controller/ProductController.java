package com.tmall.mall.controller;

import com.github.pagehelper.PageInfo;
import com.tmall.mall.service.impl.ProductServiceImpl;
import com.tmall.mall.vo.ProductDetailVo;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    ProductServiceImpl productService;

    @GetMapping("/products")
    public ResponseVo<PageInfo> list(@RequestParam(required=false) Integer categoryId,
                                     @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                                     @RequestParam(required = false,defaultValue = "10")Integer pageSize){
        return productService.list(categoryId,pageNum,pageSize);

    }

    @GetMapping("/products/{productId}")
    public ResponseVo<ProductDetailVo> detail(@PathVariable Integer productId){
        return productService.detail(productId);
    }
}
