package com.mceil.user.controller;

import com.mceil.user.pojo.Address;
import com.mceil.user.pojo.OrderPlaceContact;
import com.mceil.user.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api("用户地址接口")
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @ApiOperation("添加或者修改当前登录会员的收货地址")
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Void> saveAddress(@RequestBody Address address) {
        addressService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




    @ApiOperation("删除当前登录会员的收货地址")
    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long[] ids) {
        addressService.delete(ids);
        return  ResponseEntity.ok().build();
    }

    @ApiOperation("显示当前登录会员的所有收货地址")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Address>> list() {
        List<Address> addressList = addressService.queryAddressList();
        if(CollectionUtils.isEmpty(addressList)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(addressList);
    }

    @ApiOperation("根据当前会员登录的地址id,获取地址信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Address> findById(@PathVariable Long id) {
        Address address = addressService.getAddress(id);
        return ResponseEntity.ok(address);
    }










    @ApiOperation("收货地址设为默认")
    @GetMapping("/defaultContactAddress")
    @ResponseBody
    public ResponseEntity<Void> defaultAddress(Long id){
        addressService.defaultAddress(id);
        return ResponseEntity.ok().build();
    }

}
