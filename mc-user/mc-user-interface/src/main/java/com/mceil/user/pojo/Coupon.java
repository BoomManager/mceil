package com.mceil.user.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "tb_coupon")
// 优惠券
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "优惠卷类型；0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券")
    private Integer type;

    private String name;

    @ApiModelProperty(value = "使用平台：0->全部；1->移动；2->PC")
    private Integer platform;

    @ApiModelProperty(value = "数量")
    private Integer count;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "每人限领张数")
    private Integer perLimit;

    @ApiModelProperty(value = "使用门槛；0表示无门槛")
    private BigDecimal minPoint;
    //优惠券可以使用开始时间
    private Date startTime;
    //优惠券可以使用截止日期
    private Date endTime;

    @ApiModelProperty(value = "使用类型：0->全场通用；1->指定分类；2->指定商品")
    private Integer useType;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "发行数量")
    private Integer publishCount;

    @ApiModelProperty(value = "已使用数量")
    private Integer useCount;

    @ApiModelProperty(value = "领取数量")
    private Integer receiveCount;

    @ApiModelProperty(value = "可以领取的日期")
    private Date enableTime;

    @ApiModelProperty(value = "优惠码")
    private String code;

    @ApiModelProperty(value = "可领取的会员类型：0->无限时")
    private Integer memberLevel;

    @ApiModelProperty(value = "优惠卷使用方式：根据使用类型来存放数据，当使用类型为0时，存0，当使用类型为1时，存商品分类id，当使用类型为2时，存商品id")
    private Integer couponUse;

    @ApiModelProperty(value = "兑换所需积分")
    private Integer integrationLimit;

    @ApiModelProperty(value = "0:普通优惠劵，1：积分优惠卷")
    private Integer couponType;
}
