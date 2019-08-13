//package com.mceil.user.service.impl;
//
//
//import com.mceil.common.enums.ExceptionEnum;
//import com.mceil.common.exception.McException;
//import com.mceil.user.dto.SmsCouponGet;
//import com.mceil.user.mapper.CouponHistoryMapper;
//import com.mceil.user.mapper.CouponMapper;
//import com.mceil.user.pojo.Coupon;
//import com.mceil.user.pojo.CouponHistory;
//import com.mceil.user.pojo.User;
//import com.mceil.user.service.CouponService;
//import com.mceil.user.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//@Service
//public class CouponServiceImpl implements CouponService {
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private CouponMapper couponMapper;
//    @Autowired
//    private CouponHistoryMapper couponHistoryMapper;
//
//
//    @Override
//    public void add( Long couponId) {
//        //获取优惠券信息，判断数量
//        Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
//        //优惠券不存在
//        if (coupon == null) {
//            throw new McException(ExceptionEnum.COUPON_NOT_EXIST);
//        }
//        //优惠券已被领完
//        if (coupon.getCount() <= 0) {
//            throw new McException(ExceptionEnum.COUPON_BE_TAKEN_OVER);
//        }
//        Date now = new Date();
//        //还未到优惠券领取时间
//        if (now.before(coupon.getEnableTime())) {
//            throw new McException(ExceptionEnum.COUPON_BE_TAKEN_OVER);
//        }
//        //已过优惠券领取截止日期
//        if (now.after(coupon.getEndTime())) {
//            throw new McException(ExceptionEnum.COUPON_TIME_EXPIRED);
//        }
//        //判断用户领取的优惠券数量是否超过限制
//        int count = couponHistoryMapper.getCouponByMemberId(couponId, user.getId());
//        if (count >= coupon.getPerLimit()) {
//            throw new McException(ExceptionEnum.COUPON_WAS_RECEIVED);
//        }
//        //获取优惠券 存入数据库
//        obtainCoupon(user, now, couponId);
//        //修改优惠券表的数量、领取数量
//        coupon.setCount(coupon.getCount() - 1);
//        coupon.setReceiveCount(coupon.getReceiveCount() == null ? 1 : coupon.getReceiveCount() + 1);
//        couponMapper.updateByPrimaryKey(coupon);
//    }
//
//    //获取普通优惠劵
//    @Override
//    public List<SmsCouponGet> getCouponList(int couponType) {
//
//        //获取当前用户
//        UmsMember currentMember = getMember();
//        List<CouponHistory> couponHistoryList = new ArrayList<>();
//        //判断用户是否为空
//        if (currentMember != null) {
//            //获取用户已有优惠卷列表
//            couponHistoryList = couponHistoryMapper.getCouponListByMemberId(currentMember.getId());
//        }
//
//        List<Coupon> couponList = couponMapper.getCouponList(couponType);
//        List<SmsCouponGet> smsCouponGetList = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(couponList)) {
//            for (Coupon coupon : couponList) {
//                SmsCouponGet smsCouponGet = getSmsCouponUse(coupon, couponHistoryList);
//                smsCouponGetList.add(smsCouponGet);
//            }
//        }
//        return smsCouponGetList;
//    }
//
//    @Override
//    public List<CouponHistory> show(Long[] productIds) {
//        return null;
//    }
//
//    @Override
//    public CommonResult exChangeCoupon(User user, Long couponId) {
//        //获取当前登录用户
//        UmsMember member = getMember();
//        //根据couponId获取优惠卷
//        Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
//        if (coupon != null) {
//            //判断用户领取的优惠券数量是否超过限制
//
//            int count = couponHistoryMapper.getCouponByMemberId(couponId, member.getId());
//            if (count >= coupon.getPerLimit()) {
//                return CommonResult.failed("您已经领取过该优惠券");
//            }
//            if (member.getIntegration() >= coupon.getIntegrationLimit()) {
//                //减掉需要兑换的积分
//                member.setIntegration(member.getIntegration() - coupon.getIntegrationLimit());
//                //更新积分
//                memberMapper.updateByPrimaryKey(member);
//                //获取优惠券 存入数据库
//                obtainCoupon(user, now, couponId);
//                return CommonResult.success("兑换优惠卷成功");
//            } else {
//                return CommonResult.failed("积分不足");
//            }
//        }
//        return CommonResult.failed("兑换失败");
//    }
//
//    //获取用户领取的优惠卷和未领取的优惠卷
//    private SmsCouponGet getSmsCouponUse(Coupon coupon, List<CouponHistory> couponHistoryList) {
//        //判断couponHistoryList是否为空
//        SmsCouponGet smsCouponGet = new SmsCouponGet();
//        smsCouponGet.setCoupon(coupon);
//        //默认未领取
//        smsCouponGet.setStatus(0);
//        if (!CollectionUtils.isEmpty(couponHistoryList)) {
//            for (CouponHistory couponHistory : couponHistoryList) {
//                if (couponHistory.getCouponId() == coupon.getId()) {
//                    //设置smsCouponGet为1，0表示未领取，1表示已领取
//                    smsCouponGet.setStatus(1);
//                }
//            }
//        }
//        return smsCouponGet;
//    }
//
//    /**
//     * 16位优惠码生成：时间戳后8位+4位随机数+用户id后4位
//     */
//    private String generateCouponCode(Long memberId) {
//        StringBuilder sb = new StringBuilder();
//        Long currentTimeMillis = System.currentTimeMillis();
//        String timeMillisStr = currentTimeMillis.toString();
//        sb.append(timeMillisStr.substring(timeMillisStr.length() - 8));
//        for (int i = 0; i < 4; i++) {
//            sb.append(new Random().nextInt(10));
//        }
//        String memberIdStr = memberId.toString();
//        if (memberIdStr.length() <= 4) {
//            sb.append(String.format("%04d", memberId));
//        } else {
//            sb.append(memberIdStr.substring(memberIdStr.length() - 4));
//        }
//        return sb.toString();
//    }
//
//
//    private void obtainCoupon(User user, Date date, Long couponId) {
//        //生成领取优惠券历史
//        CouponHistory couponHistory = new CouponHistory();
//        couponHistory.setCouponId(couponId);
//        couponHistory.setCouponCode(generateCouponCode(user.getId()));
//        couponHistory.setCreateTime(date);
//        couponHistory.setMemberId(user.getId());
//        //主动领取
//        couponHistory.setGetType(1);
//        //未使用
//        couponHistory.setUseStatus(0);
//        couponHistoryMapper.insert(couponHistory);
//
//    }
//}
