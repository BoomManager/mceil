package com.mceil.item.mapper;

import com.mceil.item.pojo.Advertise;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;
@org.apache.ibatis.annotations.Mapper
public interface AdvertiseMapper extends Mapper<Advertise> , DeleteByIdListMapper<Advertise,Long> {
}
