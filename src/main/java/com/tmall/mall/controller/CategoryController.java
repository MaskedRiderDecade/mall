package com.tmall.mall.controller;

import com.tmall.mall.service.impl.CategoryServiceImpl;
import com.tmall.mall.vo.CategoryVo;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryServiceImpl categoryService;

    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> selectAll(){
        return categoryService.selectAll();
    }
}
