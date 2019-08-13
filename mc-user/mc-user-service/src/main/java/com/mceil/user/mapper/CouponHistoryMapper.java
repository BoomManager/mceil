package com.mceil.user.mapper;

import com.mceil.user.pojo.CouponHistory;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface CouponHistoryMapper extends Mapper<CouponHistory> {
    @Select("SELECT COUNT(0) FROM sms_coupon_history where coupon_id = #{couponId} and member_id = #{memberId}")
    int getCouponByMemberId(Long couponId, Long memberId);

    @Select("SELECT * FROM sms_coupon_history where member_id = #{memberId}")
    List<CouponHistory> getCouponListByMemberId(Long memberId);
}
