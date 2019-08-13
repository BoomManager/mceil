package com.mceil.user.mapper;

import com.mceil.user.pojo.MemberLevel;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MemberLevelMapper extends Mapper<MemberLevel> {
    //获取默认会员
    @Select("select * from ums_member_level where default_status = 1")
    List<MemberLevel> getMemberLevelByDefault();
}
