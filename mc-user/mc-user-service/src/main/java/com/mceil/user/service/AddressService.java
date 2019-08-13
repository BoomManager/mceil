package com.mceil.user.service;

import com.mceil.user.pojo.Address;
import com.mceil.user.pojo.OrderPlaceContact;
import com.mceil.user.pojo.User;

import java.util.List;

public interface AddressService {
    /**
     * 添加或者修改当前登录会员的收货地址
     */
    void saveAddress(Address address);

    /**
     * 删除收货地址
     * @param ids 地址表的id
     */
    void delete(Long[] ids);


    /**
     * 收货地址设置为默认
     * @param id
     * @return
     */
    void defaultAddress(Long id);

    List<Address> queryAddressList();

    Address getAddress(Long id);
}
