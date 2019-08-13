package com.mceil.collects.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.auth.pojo.UserInfo;
import com.mceil.collects.client.GoodsClient;
import com.mceil.collects.interceptor.UserInterceptor;
import com.mceil.collects.mapper.CollectsMapper;
import com.mceil.collects.pojo.Collects;
import com.mceil.collects.service.CollectsService;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.vo.PageResult;
import com.mceil.item.api.CategoryApi;
import com.mceil.item.bo.GoodsBo;
import com.mceil.item.pojo.Category;
import com.mceil.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Slf4j
@Service
public class CollectsServiceImpl implements CollectsService {
    @Autowired
    private CollectsMapper collectsMapper;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryApi categoryApi;

    @Override
    @Transactional
    public void addCollects(Long goodsId) {
        UserInfo user = getCurrentUser();
        GoodsBo goods = goodsClient.queryGoodsById(goodsId).getBody();
        if(goods == null){
            log.error(" 收藏商品失败，goodsId:{}", goodsId);
            throw new McException(ExceptionEnum.ORDER_CREATE_ERROR);
        }
        Boolean flag = isCollects(goodsId);
        if(!flag){
            Collects collects = new Collects();
            collects.setCid3(goods.getCid3());
            collects.setGoodsId(goodsId);
            collects.setCreateTime(new Date());
            collects.setUserId(user.getId());
            collectsMapper.insertSelective(collects);
        }


    }

    @Override
    public PageResult<Collects> queryCollectsPage(Integer page, Integer rows, String key) {
        UserInfo user = getCurrentUser();
        PageHelper.startPage(page,rows);
        Example example = new Example(Collects.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",user.getId());
        if(StringUtils.isNotBlank(key)){
            criteria.andEqualTo("cids",Long.parseLong(key));
        }

        List<Collects> list = collectsMapper.selectByExample(example);
        addCollectsParam(list);
        //解析分页结果
        PageInfo<Collects> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    @Transactional
    public void deleteCollects(List<Long>  ids) {
        for (Long id : ids) {
            collectsMapper.deleteByPrimaryKey(id);
        }

    }

    @Override
    public Boolean isCollects(Long goodsId) {
        Boolean flag = true;
        UserInfo user = getCurrentUser();
        Example example = new Example(Collects.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",goodsId).andEqualTo("userId",user.getId());
        List<Collects> list = collectsMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            flag = false;
        }
        return flag;
    }

    @Override
    @Transactional
    public void updateCollects(Long id, String note) {
        Collects collects = collectsMapper.selectByPrimaryKey(id);
        collects.setNoteMessage(note);
        collectsMapper.updateByPrimaryKeySelective(collects);
    }

    @Override
    public List<Category> queryCategoryList() {

        Set<Long> ids = new HashSet<>();
        UserInfo user = getCurrentUser();
        Example example = new Example(Collects.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",user.getId());
        List<Collects> collects = collectsMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(collects)){
            for (Collects collect : collects) {
                ids.add(collect.getCid3());
            }
        }
        if(!CollectionUtils.isEmpty(ids)){
            List<Category> list = categoryApi.queryCategoryByIds(new ArrayList<>(ids));
            return list;
        }
        return null;
    }

    @Override
    @Transactional
    public void batchAddCollects(List<Long> goodsIds) {
        for (Long goodsId : goodsIds) {
            addCollects(goodsId);
        }
    }

    private void addCollectsParam(List<Collects> list) {
        if(!CollectionUtils.isEmpty(list)){
            for (Collects collects : list) {
                GoodsBo goods = goodsClient.queryGoodsById(collects.getGoodsId()).getBody();
                collects.setPrices(goods.getPrices());
                //取第一张图片
                collects.setImage(StringUtils.isBlank(goods.getImages()) ? "" : StringUtils.split(goods.getImages(),",")[0]);
                collects.setGoodsSn(goods.getGoodsSn());
                collects.setTitle(goods.getTitle());
            }
        }
    }

    public UserInfo getCurrentUser() {
        return UserInterceptor.getUser();
    }
}
