package com.mceil.item.mapper;

import com.mceil.item.pojo.Goods;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;
@org.apache.ibatis.annotations.Mapper
public interface GoodsMapper extends Mapper<Goods> , SelectByIdListMapper<Goods,Long>, DeleteByIdListMapper<Goods,Long> {
}
