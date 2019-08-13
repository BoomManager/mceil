package com.mceil.item.web;

import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.Coupon;
import com.mceil.item.service.CouponService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("优惠券服务接口")
@RestController
@RequestMapping("coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    /**
     * 分页查询品牌
     * @param page
     * @param rows
     * @return
     */
    @ApiOperation(value = "分页查询优惠券", notes = "分页查询优惠券,返回分页后的优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = false, value = "分页查询所需要的页数(当前页),默认值为1"),
            @ApiImplicitParam(name = "rows", required = false, value = "一页共有几条数据，默认值为5"),
    })
    @ApiResponse(code = 404, message = "品牌不存在")
    @GetMapping("page")
    public ResponseEntity<PageResult<Coupon>> queryCouponPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(name = "rows", defaultValue = "5") Integer rows){
        PageResult<Coupon> result = couponService.queryCouponPage(page,rows);
        if(CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "添加优惠券", notes = "添加优惠券")
    @PostMapping
    public ResponseEntity<Void> addCoupon(@RequestBody Coupon coupon){
        couponService.addCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @ApiOperation(value = "更改优惠券", notes = "更改优惠券")
    @PutMapping
    public ResponseEntity<Void> updateCoupon(@RequestBody Coupon coupon){
        couponService.updateCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "更改优惠券", notes = "更改优惠券")
    @DeleteMapping
    public ResponseEntity<Void> deleteCoupon(@RequestParam("ids")List<Long> ids){
        couponService.deleteCoupon(ids);
        return ResponseEntity.ok().build();
    }
}
