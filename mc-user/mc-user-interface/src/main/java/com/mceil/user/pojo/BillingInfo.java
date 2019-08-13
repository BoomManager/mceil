package com.mceil.user.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_billing")
//开票信息
public class BillingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    //开票公司名称
    private String companyName;
    //税号
    private String taxpay_num;
    //地址
    private String address;
    //公司电话
    private String telephone;
    //对公账户
    private String accountNum;
//    //发票接收人
//    private String bill_receive;
//    //发票接收人电话
//    private String phoneReceive;
//    //发票接收人地址
//    private String receiveAddress;
    //审核状态
    private int checkStatus;
}
