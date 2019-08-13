//package com.mceil.user.service.impl;
//
//import com.mceil.user.mapper.BillingInfoMapper;
//import com.mceil.user.pojo.BillingInfo;
//import com.mceil.user.pojo.User;
//import com.mceil.user.service.BillingInfoService;
//import com.mceil.user.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import tk.mybatis.mapper.entity.Example;
//
//import java.util.List;
//
//@Service
//public class BillingInfoServiceImpl implements BillingInfoService {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private BillingInfoMapper billingInfoMapper;
//
//    @Override
//    @Transactional
//    public void saveBill(BillingInfo billingInfo) {
//        //设置审核状态为未审核
//        billingInfo.setCheckStatus(0);
//        //设置发票所属用户
//        billingInfo.setMemberId(user.getId());
//        billingInfoMapper.insertSelective(billingInfo);
//    }
//
//    @Override
//    public List<BillingInfo> getBillListByMember() {
//
//        Example example = new Example(BillingInfo.class);
//        example.createCriteria().andEqualTo("memberId", user.getId());
//        return billingInfoMapper.selectByExample(example);
//    }
//
//    @Override
//    @Transactional
//    public void checkBill(Long id, int checkStatus) {
//        BillingInfo billingInfo = billingInfoMapper.selectByPrimaryKey(id);
//        //修改审核状态
//        billingInfo.setCheckStatus(checkStatus);
//        billingInfoMapper.updateByPrimaryKeySelective(billingInfo);
//    }
//
//
//}
