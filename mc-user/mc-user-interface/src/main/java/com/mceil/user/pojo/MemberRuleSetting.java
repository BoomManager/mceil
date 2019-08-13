package com.mceil.user.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "tb_member_rule_setting")
public class MemberRuleSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   //每消费多少元获取1个点
    private BigDecimal consumePerPoint;

    //最低获取点数的订单金额
    private BigDecimal lowOrderAmount;

    //每笔订单最高获取点数
    private Integer maxPointPerOrder;

    //类型：0->积分规则；1->成长值规则
    private Integer type;


}