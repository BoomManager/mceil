//package com.mceil.user.controller;
//
//import com.mceil.common.common.CommonResult;
//import com.mceil.user.service.CollectionsService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.List;
//
//@Controller
//@Api(tags = "MemberCollectionsController", description = "会员收藏管理")
//@RequestMapping("/user/collections")
//public class CollectionsController {
//    @Autowired
//    private CollectionsService collectionsService;
//
//    @ApiOperation("添加会员收藏")
//    @GetMapping("/add/{ids}")
//    @ResponseBody
//    public CommonResult addCollections(@PathVariable List<Long> ids){
//        if(collectionsService.addProduct(ids) > 0){
//            return CommonResult.success("收藏成功！");
//        }
//        return CommonResult.failed("收藏失败");
//    }
//    @ApiOperation("添加会员收藏")
//    @GetMapping("/delete/{ids}")
//    @ResponseBody
//    public CommonResult deleteCollections(@PathVariable List<Long> ids){
//        if(collectionsService.deleteProduct(ids) > 0){
//            return CommonResult.success("删除成功！");
//        }
//        return CommonResult.failed("删除失败");
//    }
//}
