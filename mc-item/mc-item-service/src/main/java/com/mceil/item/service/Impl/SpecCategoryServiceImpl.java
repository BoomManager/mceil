package com.mceil.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.vo.PageResult;
import com.mceil.item.mapper.SpecCategoryMapper;
import com.mceil.item.mapper.SpecValueMapper;
import com.mceil.item.pojo.SpecCategory;
import com.mceil.item.pojo.SpecValue;
import com.mceil.item.service.SpecCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class SpecCategoryServiceImpl implements SpecCategoryService {
    @Autowired
    private SpecCategoryMapper specCategoryMapper;
    @Autowired
    private SpecValueMapper specValueMapper;
    @Override
    @Transactional
    public void addSpecCategory(Long cid) {
        SpecCategory specCategory = new SpecCategory();
        specCategory.setCid(cid);
        SpecCategory record = specCategoryMapper.selectOne(specCategory);
        if(record == null){
            specCategoryMapper.insertSelective(specCategory);
        }
    }


    @Override
    @Transactional
    public void deleteSpecCategory(Long cid) {
        //先获取specId
        SpecCategory specCategory = querySpecCategoryByCid(cid);
        String specIdsStr = specCategory.getSpecIds();
        List<Long> idsList = new ArrayList<>();
        //包含“，”说明存放的不止一个specIds
        if(StringUtils.isNotBlank(specIdsStr)){
            //包含多个specId
            if(specIdsStr.contains(",")){
                String[] ids = specIdsStr.split(",");
                //遍历，转成Long类型
                for (int i = 0; i < ids.length ; i++){
                    Long specId = Long.parseLong(ids[i]);
                    idsList.add(specId);
                }
            }else{
                //只有一个specId
                idsList.add(Long.parseLong(specIdsStr));
            }
            //删除属性规格值
            specValueMapper.deleteByIdList(idsList);
        }

        //再删除SpecCategory
        specCategoryMapper.delete(specCategory);

    }

    @Override
    public PageResult<SpecCategory> querySpecCategoryPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        List<SpecCategory> list = specCategoryMapper.selectAll();
        PageInfo<SpecCategory> info = new PageInfo<>(list);
        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    public SpecCategory querySpecCategoryByCid(Long cid) {
        SpecCategory specCategory = new SpecCategory();
        specCategory.setCid(cid);
        SpecCategory record = specCategoryMapper.selectOne(specCategory);
        return record;
    }
}
