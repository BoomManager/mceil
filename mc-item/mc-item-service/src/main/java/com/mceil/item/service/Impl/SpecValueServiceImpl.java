package com.mceil.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.SpecValueBo;
import com.mceil.item.bo.SpecValueDto;
import com.mceil.item.mapper.CategoryMapper;
import com.mceil.item.mapper.SpecCategoryMapper;
import com.mceil.item.mapper.SpecValueMapper;
import com.mceil.item.pojo.Category;
import com.mceil.item.pojo.SpecCategory;
import com.mceil.item.pojo.SpecValue;
import com.mceil.item.service.SpecCategoryService;
import com.mceil.item.service.SpecValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpecValueServiceImpl implements SpecValueService {
    @Autowired
    private SpecValueMapper specValueMapper;
    @Autowired
    private SpecCategoryMapper specCategoryMapper;
    @Autowired
    private SpecCategoryService specCategoryService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    @Transactional
    public void addSpecValue(SpecValueDto specValueDto) {
        List<SpecValue> specValueList = specValueDto.getSpecValueList();
        if(!CollectionUtils.isEmpty(specValueList)){
            //遍历，然后插入数据库
            for (SpecValue specValue : specValueList) {
                specValueMapper.insertSelective(specValue);
            }
            //获取specIds集合
            Set<Long> specIds = specValueList.stream().map(SpecValue :: getSpecId).collect(Collectors.toSet());
            String specIdsStr = "";
            //将specIds以'，'分开转成字符串
            if(specIds.size() > 1){
               specIdsStr  = StringUtils.join(specIds,",");
            }else {
                //只有一个specId时
                specIdsStr = StringUtils.join(specIds,"");
            }
            SpecCategory specCategory = specCategoryService.querySpecCategoryByCid(specValueDto.getCid());
            specCategory.setSpecIds(specIdsStr);
            specCategoryMapper.updateByPrimaryKeySelective(specCategory);
        }

    }

    @Override
    @Transactional
    public void updateSpecValue(SpecValue specValue) {
        specValueMapper.updateByPrimaryKey(specValue);
    }

    @Override
    @Transactional
    public void deleteSpecValue(Long id,Long cid) {
        SpecCategory record = new SpecCategory();
        record.setCid(cid);
        SpecCategory specCategory = specCategoryMapper.selectOne(record);
        String specIdsStr = specCategory.getSpecIds();
        List<Long> idsList = new ArrayList<>();
        //包含“，”说明存放的不止一个specIds
        if(specIdsStr.contains(",") && specIdsStr.contains(id.toString())){
            //将specId替换成空字符，顺便去掉“，”
            specIdsStr.replace(","+id,"");
        }else{
            specIdsStr = "";
        }
        specCategory.setSpecIds(specIdsStr);
        //更新SpecCategory
        specCategoryMapper.updateByPrimaryKeySelective(specCategory);
    }



    @Override
    public List<SpecValue> querySpecValueListByCid(Long cid) {
        SpecCategory specCategory = new SpecCategory();
        //根据cid获取SpecCategory
        SpecCategory record = specCategoryService.querySpecCategoryByCid(cid);
        if(record == null){
            //获取其分类
            Category category = categoryMapper.selectByPrimaryKey(cid);
            //获取其父分类的规格属性
            specCategory = specCategoryService.querySpecCategoryByCid(category.getParentId());
        }else {
            specCategory = record;
        }
        if(specCategory != null){
            //获取SpecIds
            String specIds = specCategory.getSpecIds();
            //存放SpecIds集合
            List<Long> specIdsList = new ArrayList<Long>();
            if(StringUtils.isNotBlank(specIds)){
                //判断specIds是否有“，”，有，则不止存在一个specId，没有，则只有一个
                if(specIds.contains(",")){
                    String[] ids = specIds.split(",");
                    //遍历，转成Long类型
                    for (int i = 0; i < ids.length ; i++){
                        Long specId = Long.parseLong(ids[i]);
                        specIdsList.add(specId);
                    }
                }
            }
            //获取该分类下的所有属性及属性值
            if(!CollectionUtils.isEmpty(specIdsList)){
                Example example = new Example(SpecValue.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("specId",specIdsList);
                List<SpecValue> specValueList = specValueMapper.selectByExample(example);
                return specValueList;
            }
        }
        return null;
    }

}
