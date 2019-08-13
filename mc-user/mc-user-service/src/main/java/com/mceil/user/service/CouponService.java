package com.mceil.user.service;

import com.mceil.user.dto.SmsCouponGet;
import com.mceil.user.pojo.CouponHistory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CouponService {
    // 会员添加优惠券
    @Transactional
    void add(Long couponId);

    //优惠券列表
    List<SmsCouponGet> getCouponList(int couponType);

    //查看优惠券领取记录
    List<CouponHistory> show(Long[] productIds);

    //使用优惠券
    Boolean exChangeCoupon(Long couponId);

}
