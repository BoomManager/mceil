package com.mceil.user.pojo;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_integration_consume_setting")
public class IntegrationConsumeSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //每一元需要抵扣的积分数量
    private Integer deductionPerAmount;

    //每笔订单最高抵用百分比
    private Integer maxPercentPerOrder;

    //每次使用积分最小单位100
    private Integer useUnit;

    //是否可以和优惠券同用；0->不可以；1->可以
    private Integer couponStatus;


}