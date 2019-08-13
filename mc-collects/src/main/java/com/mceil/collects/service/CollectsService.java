package com.mceil.collects.service;

import com.mceil.collects.pojo.Collects;
import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.Category;

import java.util.List;

public interface CollectsService {
    void addCollects(Long goodsId);

    PageResult<Collects> queryCollectsPage(Integer page, Integer rows, String key);

    void deleteCollects(List<Long> ids);

    Boolean isCollects(Long goodsId);

    void updateCollects(Long id, String note);

    List<Category> queryCategoryList();

    void batchAddCollects(List<Long> goodsIds);
}
