//package com.mceil.user.controller;
//
//import com.mceil.user.pojo.BillingInfo;
//import com.mceil.user.service.BillingInfoService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@Api(tags = "BillController", description = "发票管理")
//@RequestMapping("/user/bill")
//public class BillController {
//    @Autowired
//    private BillingInfoService billingInfoService;
//    @ApiOperation("保存发票")
//    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    public ResponseEntity<Void> addBill(@RequestParam BillingInfo billingInfo){
//        this.billingInfoService.saveBill(billingInfo);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//    @ApiOperation("获取当前登录人的发票列表")
//    @RequestMapping(value = "/getBillList", method = RequestMethod.POST)
//    public ResponseEntity<List<BillingInfo>> getBillList(){
//        List<BillingInfo> list = this.billingInfoService.getBillListByMember();
//        if(CollectionUtils.isEmpty(list)){
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return ResponseEntity.ok(list);
//    }
//}
