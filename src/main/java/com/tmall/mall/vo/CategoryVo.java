package com.tmall.mall.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Integer id;
    private Integer parent_id;
    private String  name;
    private Integer sortOrder;

    private List<CategoryVo> subCategories;


}
