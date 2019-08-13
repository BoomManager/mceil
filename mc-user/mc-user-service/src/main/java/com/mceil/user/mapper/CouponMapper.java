package com.mceil.user.mapper;

import com.mceil.user.pojo.Coupon;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface CouponMapper extends Mapper<Coupon> {
    //获取普通优惠卷
    @Select("select * from sms_coupon where coupon_type = #{couponType}")
    List<Coupon> getCouponList(int couponType);
}
