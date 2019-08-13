package com.mceil.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.vo.PageResult;
import com.mceil.item.mapper.AdvertiseMapper;
import com.mceil.item.pojo.Advertise;
import com.mceil.item.service.AdvertiseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AdvertiseServiceImpl implements AdvertiseService {
    @Autowired
    private AdvertiseMapper advertiseMapper;

    @Override
    public PageResult<Advertise> queryAdvertisePage(Integer page, Integer rows, String name, Date endTime) {
        PageHelper.startPage(page,rows);
        
        Example example = new Example(Advertise.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(name)){
            criteria.andLike("name","%"+name+"%");
        }
        if(endTime != null){
            criteria.andLessThanOrEqualTo("endTime",endTime);
        }
        List<Advertise> list = advertiseMapper.selectByExample(example);

        //解析分页结果
        PageInfo<Advertise> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    @Transactional
    public void saveAdvertise(Advertise advertise) {
        advertiseMapper.insertSelective(advertise);
    }

    @Override
    @Transactional
    public void updateAdvertise(Advertise advertise) {
        advertiseMapper.updateByPrimaryKeySelective(advertise);
    }

    @Override
    @Transactional
    public void deleteAdvertise(List<Long> ids) {
        advertiseMapper.deleteByIdList(ids);
    }

    @Override
    public Advertise queryAdvertiseById(Long id) {
        return advertiseMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageResult<Advertise> queryAdvertisePagePortal(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Advertise.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",1);
        List<Advertise> list = advertiseMapper.selectByExample(example);
        //解析分页结果
        PageInfo<Advertise> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }
}
