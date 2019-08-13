package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.SpecValueBo;
import com.mceil.item.bo.SpecValueDto;
import com.mceil.item.pojo.SpecValue;

import java.util.List;

public interface SpecValueService {

    void addSpecValue(SpecValueDto specValueDto);

    void updateSpecValue(SpecValue specValue);

    void deleteSpecValue(Long id,Long cid);

    List<SpecValue> querySpecValueListByCid(Long cid);


}
