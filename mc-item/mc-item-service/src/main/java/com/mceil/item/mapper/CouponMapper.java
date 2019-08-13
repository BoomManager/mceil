package com.mceil.item.mapper;

import com.mceil.item.pojo.Coupon;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponMapper extends Mapper<Coupon> , DeleteByIdListMapper<Coupon, Long> {
}
