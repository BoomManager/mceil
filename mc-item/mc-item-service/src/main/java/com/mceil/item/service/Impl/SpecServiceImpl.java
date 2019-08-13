package com.mceil.item.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.utils.JsonUtils;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.SpecStatus;
import com.mceil.item.bo.SpecValueBo;
import com.mceil.item.mapper.CategoryMapper;
import com.mceil.item.mapper.GoodsMapper;
import com.mceil.item.mapper.SpecMapper;
import com.mceil.item.pojo.Category;
import com.mceil.item.pojo.Goods;
import com.mceil.item.pojo.Spec;
import com.mceil.item.service.GoodsService;
import com.mceil.item.service.SpecService;

import org.apache.commons.lang3.StringUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;


import java.util.*;

@Service
public class SpecServiceImpl implements SpecService {
    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Override
    public Spec querySpecByCid(Long cid) {
        Spec specSql = getSpec(cid);
        if(specSql == null){
            //获取父级分类模板
            Category category = categoryMapper.selectByPrimaryKey(cid);
            if(category != null){
                Spec specTemp = new Spec();
                //如果是三级分类，三级分类如果没有属性，则获取父分类的属性
                if(category.getLevel().longValue() == 3L){
                    //specTemp = specMapper.querySpecByCid(category.getParentId()).get(0);
                    specTemp = getSpec(category.getParentId());
                }

                return specTemp;
            }

        }
        return specSql;
    }
    //根据分类id获取模板
    public Spec getSpec(Long cid){
        Spec record = new Spec();
        record.setCid(cid);
        Spec spec = specMapper.selectOne(record);
        return spec;
    }
    @Override
    public Spec querySpecById(Long id) {
        return specMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void saveSpec(Spec spec) {

        Spec specSql = specMapper.selectOne(spec);
        if(specSql == null){

            specMapper.insert(spec);
        }
        //throw new McException(ExceptionEnum.SPEC_GROUP_ADD_ERROR);
    }
    public Spec getSpecByCid(Long cid){
        Spec spec = new Spec();
        spec.setCid(cid);
        return specMapper.selectOne(spec);
    }
    @Override
    @Transactional
    public void updateSpec(Spec spec) {
        //先获取修改之前的数据
        Spec record = specMapper.selectByPrimaryKey(spec.getId());

        //判断是否修改了分类id
        if(record.getCid().longValue() == spec.getCid().longValue()){

            specMapper.updateByPrimaryKey(spec);
        }else{
            //通过cid去查询是否有数据，没有就能更新，有就抛异常
            Spec specSql = getSpecByCid(spec.getCid());
            if(specSql == null){
                specMapper.updateByPrimaryKeySelective(spec);
            }
            throw new McException(ExceptionEnum.SPEC_GROUP_UPDATE_ERROR);
        }
    }


    @Override
    @Transactional
    public void deleteSpec(List<Long> ids) {
        try{
            specMapper.deleteByIdList(ids);
        }catch (Exception e){
            e.printStackTrace();
            throw new McException(ExceptionEnum.SPEC_GROUP_DELETE_ERROR);
        }

    }

    @Override
    public PageResult<Spec> queryAllSpec(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Spec.class);
        List<Spec> list = specMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)){
            for (Spec spec : list) {
                Category category = categoryMapper.selectByPrimaryKey(spec.getCid());
                if(category.getLevel().longValue() == 2L){
                    spec.setCname(category.getName());
                }else {
                    //三级分类，获取父分类名称
                    Category record = categoryMapper.selectByPrimaryKey(category.getParentId());
                    List<String> cnameList = new ArrayList<>();
                    cnameList.add(record.getName());
                    cnameList.add(category.getName());
                    spec.setCname(StringUtils.join(cnameList,"/"));
                }
            }
        }
        //解析分页结果
        PageInfo<Spec> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    public List<SpecValueBo> querySpecByValue(Long cid, String values) {
        Spec spec = querySpecByCid(cid);
        List<SpecValueBo> valuelist = new ArrayList<>();
        //1.将规格反序列化为集合
        List<Map<String,Object>> specs = null;
        specs = JsonUtils.nativeRead(spec.getSpecTemplate(), new TypeReference<List<Map<String, Object>>>() {
        });
        Set<String> goodsValue = new HashSet<>();
        //根据分类id获取商品列表
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cid2",cid).orEqualTo("cid3",cid);
        if(StringUtils.isNotBlank(values)){
            if(values.contains(",")){
                String[] valueArray = values.split(",");
                List<String> valueList = new ArrayList<>(Arrays.asList(valueArray));
                for (int i = 0; i < valueList.size(); i++) {
                    String value = valueList.get(i);
                    criteria.andLike("ownSpec","%" + value + "%");
                }
            }
        }
        List<Goods> goodsList = goodsMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(goodsList)){
            for (Goods goods : goodsList) {
                //1.将规格反序列化为集合
                List<Map<String,Object>> specValue = null;
                specValue = JsonUtils.nativeRead(goods.getOwnSpec(), new TypeReference<List<Map<String, Object>>>() {
                });
                specValue.forEach( val -> {
                    List<String> list = (List<String>) val.get("dynamicTags");
                    for (String s : list) {
                        goodsValue.add(s);
                    }
                });
            }
        }
        //构建商品特有属性
        specs.forEach( param ->{
            SpecValueBo specValueBo = new SpecValueBo();
            String key = (String) param.get("name");
            specValueBo.setName(key);
            List<String> list = (List<String>) param.get("dynamicTags");
            List<SpecStatus> specStatuses = new ArrayList<>();
            for (String s : list) {
                SpecStatus specStatus = new SpecStatus();
                specStatus.setValue(s);
                if(StringUtils.isBlank(values)){
                    specStatus.setFlag(true);
                }else{
                    if (goodsValue.contains(s)){
                        specStatus.setFlag(true);
                    }else{
                        specStatus.setFlag(false);
                    }
                }

                specStatuses.add(specStatus);
            }
            specValueBo.setSpecStatusList(specStatuses);
            valuelist.add(specValueBo);
        });

        return valuelist;
    }



    //合并两个jsonarray
/*    private static JSONArray joinJSONArray(JSONArray array1, JSONArray array2) {
        StringBuffer sbf = new StringBuffer();
        JSONArray jSONArray = new JSONArray();
        try {
            int len = array1.size();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array1.get(i);
                if (i == len - 1)
                    sbf.append(obj1.toString());
                else
                    sbf.append(obj1.toString()).append(",");
            }
            len = array2.size();
            if (len > 0)
                sbf.append(",");
            for (int i = 0; i < len; i++) {
                JSONObject obj2 = (JSONObject) array2.get(i);
                if (i == len - 1)
                    sbf.append(obj2.toString());
                else
                    sbf.append(obj2.toString()).append(",");
            }

            sbf.insert(0, "[").append("]");
            jSONArray = jSONArray.parseArray(sbf.toString());
            return jSONArray;
        } catch (Exception e) {
        }
        return null;
    }*/
}
