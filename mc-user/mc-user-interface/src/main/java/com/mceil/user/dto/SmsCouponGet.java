package com.mceil.user.dto;

import com.mceil.user.pojo.Coupon;
import lombok.Data;

@Data
public class SmsCouponGet {
    private Coupon coupon;
    //0表示未领取，1表示已领取
    int status;
}
