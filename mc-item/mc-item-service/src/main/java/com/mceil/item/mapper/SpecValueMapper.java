package com.mceil.item.mapper;

import com.mceil.item.pojo.SpecValue;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;
@org.apache.ibatis.annotations.Mapper
public interface SpecValueMapper extends Mapper<SpecValue> , DeleteByIdListMapper<SpecValue,Long> {
}
