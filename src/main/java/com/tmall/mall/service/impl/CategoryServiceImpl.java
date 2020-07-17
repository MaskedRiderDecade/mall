package com.tmall.mall.service.impl;

import com.tmall.mall.dao.CategoryMapper;
import com.tmall.mall.pojo.Category;
import com.tmall.mall.service.ICategoryService;
import com.tmall.mall.vo.CategoryVo;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.tmall.mall.consts.MallConst.ROOT_PARENT_ID;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List <CategoryVo> categoryVoList=new ArrayList<>();
        List<Category> categories = categoryMapper.selectAll();
        //查出parent_id=0
        for(Category category:categories){
            if(category.getParentId().equals(ROOT_PARENT_ID)){
                CategoryVo categoryVo=new CategoryVo();
                BeanUtils.copyProperties(category,categoryVo);
                categoryVoList.add(categoryVo);
            }
        }

        findSubCategory(categoryVoList,categories);
        categoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());

        return ResponseVo.success(categoryVoList);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category>categories=categoryMapper.selectAll();
        findSubCategoryId(id,resultSet,categories);

    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet,List<Category> categories) {
        for (Category category : categories) {
            if(category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categories);
            }
        }
    }

    private void findSubCategory(List<CategoryVo> categoryVoList,List<Category> categories){
        for (CategoryVo categoryVo : categoryVoList) {
            List<CategoryVo>subCategoryVoList=new ArrayList<>();
            for (Category category : categories) {
//                如果查到了，设置子目录，继续往下查
                if(categoryVo.getId().equals(category.getParentId())){
                    CategoryVo subCategoryVo = category2categoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }

                subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
                categoryVo.setSubCategories(subCategoryVoList);
                findSubCategory(subCategoryVoList,categories);
            }
        }


    }

    private CategoryVo category2categoryVo(Category category){
        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}
