package com.mceil.user.service;

import com.mceil.user.pojo.BillingInfo;

import java.util.List;

public interface BillingInfoService {
    //添加发票
    void saveBill(BillingInfo billingInfo);
    //获取当前用户增值税发票列表
    List<BillingInfo> getBillListByMember();
    //审核发票
    void checkBill(Long id, int checkStatus);
}
