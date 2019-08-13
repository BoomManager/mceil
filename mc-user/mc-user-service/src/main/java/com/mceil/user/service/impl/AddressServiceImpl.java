package com.mceil.user.service.impl;

import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.user.mapper.AddressMapper;
import com.mceil.user.mapper.OrderPlaceContactMapper;
import com.mceil.user.pojo.Address;
import com.mceil.user.pojo.OrderPlaceContact;
import com.mceil.user.pojo.User;
import com.mceil.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public User getUser(){
        //测试时用
        User user = new User();
        user.setId(30L);
        return user;
    }
    @Override
    @Transactional
    public void saveAddress(Address address) {
        User user = getUser();
        address.setUserId(user.getId());
        if(address.getId() != null){
            //有地址id为修改
            addressMapper.updateByPrimaryKey(address);
        }else {
            addressMapper.insertSelective(address);
        }

    }

    @Override
    @Transactional
    public void delete(Long[] ids) {
        for (Long id : ids) {
            addressMapper.deleteByPrimaryKey(id);
        }
    }



    @Override
    @Transactional
    public void defaultAddress(Long id) {
        //修改当前用户的默认地址为0
        User user = getUser();
        //先获取默认地址
        Example example = new Example(Address.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",user.getId()).andEqualTo("defaultStatus",1);
        List<Address> list = addressMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)){
            //当前用户有默认地址，修改默认地址
            //将之前的默认地址设置为0
            Address address = list.get(0);
            address.setDefaultStatus(0);
            addressMapper.updateByPrimaryKeySelective(address);
        }

        addressMapper.updateDefaultAddress(id);
    }

    @Override
    public List<Address> queryAddressList() {
        User user = getUser();
        Example example = new Example(Address.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",user.getId());
        List<Address> list = addressMapper.selectByExample(example);
        return list;
    }

    @Override
    public Address getAddress(Long id) {
        return addressMapper.selectByPrimaryKey(id);
    }
}
