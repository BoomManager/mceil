package com.mceil.item.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "tb_coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer type;
    private String name;
    private Integer platform;
    private Integer count;
    private BigDecimal amount;
    private Integer perLimit;
    private BigDecimal minPoint;
    private Date startTime;
    private Date endTime;
    private Integer useType;
    private String note;
    private Integer publishCount;
    private Integer userCount;
    private Integer receiveCount;
    private Date enableTime;
    private String code;
    private Integer userLevel;
}
