package com.mceil.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.vo.PageResult;
import com.mceil.item.mapper.CouponMapper;
import com.mceil.item.pojo.Brand;
import com.mceil.item.pojo.Coupon;
import com.mceil.item.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Override
    @Transactional
    public void addCoupon(Coupon coupon) {
        couponMapper.insertSelective(coupon);
    }

    @Override
    @Transactional
    public void updateCoupon(Coupon coupon) {
        couponMapper.updateByPrimaryKeySelective(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(List<Long> ids) {
        couponMapper.deleteByIdList(ids);
    }

    @Override
    public Coupon queryCouponById(Long id) {
        return couponMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Coupon> queryCouponList() {
        return couponMapper.selectAll();
    }

    @Override
    public PageResult<Coupon> queryCouponPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        //获取所有的优惠卷
        List<Coupon> list = couponMapper.selectAll();
        //构建分页对象
        PageInfo<Coupon> info = new PageInfo<>(list);
        return new PageResult<>(info.getTotal(), list);
    }
}
