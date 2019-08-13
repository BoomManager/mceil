package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.Coupon;

import java.util.List;

public interface CouponService {

    void addCoupon(Coupon coupon);

    void updateCoupon(Coupon coupon);

    void deleteCoupon(List<Long> ids);

    Coupon queryCouponById(Long id);

    List<Coupon> queryCouponList();

    PageResult<Coupon> queryCouponPage(Integer page,Integer rows);
}
