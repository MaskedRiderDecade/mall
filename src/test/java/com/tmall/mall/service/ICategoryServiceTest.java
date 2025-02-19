package com.tmall.mall.service;

import com.tmall.mall.MallApplicationTests;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.service.impl.CategoryServiceImpl;
import com.tmall.mall.vo.CategoryVo;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ICategoryServiceTest extends MallApplicationTests {

    @Autowired
    CategoryServiceImpl categoryService;

    @Test
    public void selectAll() {
        ResponseVo<List<CategoryVo>>responseVo=categoryService.selectAll();
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }


    @Test
    public void findSubCategoryId() {
        Set<Integer>set=new HashSet<>();
        categoryService.findSubCategoryId(100001,set);
        log.info("set={}",set);
    }
}