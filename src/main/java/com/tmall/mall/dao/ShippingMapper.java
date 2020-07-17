package com.tmall.mall.dao;

import com.tmall.mall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    //不使用@Param注解，参数只能有一个并且是JavaBean类型
    Integer deleteByIdAndUid(@Param("uid") Integer uid,@Param("shippingId") Integer shippingId);

    List<Shipping> selectByUid(Integer uid);

    Shipping selectByUidAndShippingId(@Param("uid") Integer uid,@Param("shippingId") Integer shippingId);

    List<Shipping> selectByIdSet(@Param("idSet") Set idSet);
}