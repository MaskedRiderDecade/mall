package com.tmall.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tmall.mall.dao.ProductMapper;
import com.tmall.mall.enums.ProductStatusEnum;
import com.tmall.mall.pojo.Product;
import com.tmall.mall.service.IProductService;
import com.tmall.mall.vo.ProductDetailVo;
import com.tmall.mall.vo.ProductVo;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tmall.mall.enums.ResponseEnum.PRODUCT_OFFSALE_OR_DELETE;

@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet=new HashSet<>();
        if(categoryId!=null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVoList = productList.stream()
                .map(e -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(e, productVo);
                    return productVo;
                })
                .collect(Collectors.toList());
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productVoList);
        return ResponseVo.success(pageInfo);


    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product.getStatus().equals( ProductStatusEnum.OFF_SALE.getCode())||product.getStatus().equals( ProductStatusEnum.DELETE.getCode())){
            return ResponseVo.error(PRODUCT_OFFSALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo=new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        return ResponseVo.success(productDetailVo);
    }
}
