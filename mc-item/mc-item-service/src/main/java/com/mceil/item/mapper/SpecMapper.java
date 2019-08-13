package com.mceil.item.mapper;

import com.mceil.item.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecMapper extends Mapper<Spec> , DeleteByIdListMapper<Spec,Long> {
    @Select("SELECT s.`id`,s.`cid`,s.`spec_template` FROM tb_spec s where s.cid = #{cid}")
    List<Spec> querySpecByCid(@Param("cid") Long cid);
}
