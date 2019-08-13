package com.mceil.item.mapper;

import com.mceil.item.pojo.GoodsDetail;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface GoodsDetailMapper extends Mapper<GoodsDetail>, DeleteByIdListMapper<GoodsDetail,Long> {
}
