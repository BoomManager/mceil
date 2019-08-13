package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.SpecCategory;

public interface SpecCategoryService {

    void addSpecCategory(Long cid);


    void deleteSpecCategory(Long cid);

    PageResult<SpecCategory> querySpecCategoryPage(Integer page,Integer rows);

    SpecCategory querySpecCategoryByCid(Long cid);
}
