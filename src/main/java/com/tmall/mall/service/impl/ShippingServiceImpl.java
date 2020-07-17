package com.tmall.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tmall.mall.dao.ShippingMapper;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.form.ShippingForm;
import com.tmall.mall.pojo.Shipping;
import com.tmall.mall.service.IShippingService;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        shipping.setUserId(uid);
        int row = shippingMapper.insertSelective(shipping);
        if(row==0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        Map<String, Integer>map=new HashMap<>();
        map.put("shippingId",shipping.getId());
        log.info("id={}",shipping.getId());
        return ResponseVo.success(map);

    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {
        Integer row = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if(row==0){
            return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if(row==0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();

    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUid(uid);
        //因为是数据库里直接查出来的类型，所以直接用构造方法，否则应该使用setList();
        PageInfo pageInfo=new PageInfo(shippingList);
        return ResponseVo.success(pageInfo);

    }
}
