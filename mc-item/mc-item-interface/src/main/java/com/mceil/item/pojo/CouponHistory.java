package com.mceil.item.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name="tb_coupon_history")
public class CouponHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private Long userId;
    private String couponCode;
    private String userNickname;
    private Integer getType;
    private Date createTime;
    private Integer userStatus;
    private Date userTime;
    private Long orderId;

}
