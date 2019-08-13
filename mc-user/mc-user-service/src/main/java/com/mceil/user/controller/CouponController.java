//package com.mceil.user.controller;
//
//import com.mceil.common.common.CommonResult;
//import com.mceil.user.dto.SmsCouponGet;
//import com.mceil.user.pojo.CouponHistory;
//
//import com.mceil.user.service.CouponService;
//import com.mceil.user.service.MemberService;
//import io.swagger.annotations.Api;
//
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@Api(tags = "CouponController", description = "用户优惠券管理")
//@RequestMapping("/user/coupon")
//public class CouponController {
//
//    @Autowired
//    private CouponService memberCouponService;
//
//    @Autowired
//    private MemberService memberService;
//
//
//
//    @ApiOperation("会员领取指定优惠券")
//    @RequestMapping(value = "/add/{couponId}", method = RequestMethod.POST)
//    public CommonResult add(@PathVariable Long couponId) {
//        return memberCouponService.add(couponId);
//    }
//
//    @ApiOperation("提交订单页面中的展示会员的优惠券")
//    @RequestMapping(value = "/show", method = RequestMethod.POST)
//    public CommonResult show(Long[] productIds) {
//        List<CouponHistory> smsCouponHistories = memberCouponService.show(productIds);
//        return CommonResult.success(smsCouponHistories);
//    }
//
//
//
//
//    @ApiOperation("优惠券列表")
//    @GetMapping("/couponList")
//    public CommonResult<List<SmsCouponGet>> couponList(){
//        //0表示普通优惠卷
//        List<SmsCouponGet> smsCouponGets = memberCouponService.getCouponList(0);
//        return  CommonResult.success(smsCouponGets);
//    }
//
//    @ApiOperation("兑换优惠卷")
//    @GetMapping("/exchange")
//    public CommonResult exchange(@RequestParam Long couponId){
//        CommonResult result = memberCouponService.exChangeCoupon(couponId);
//        return result;
//    }
//    @ApiOperation("获取积分优惠卷")
//    @GetMapping("/integrationCoupon")
//    public CommonResult<List<SmsCouponGet>> integrationCoupon(){
//        //1表示积分优惠卷
//        List<SmsCouponGet> smsCouponGets = memberCouponService.getCouponList(1);
//        return  CommonResult.success(smsCouponGets);
//    }
//}
