package com.mceil.user.mapper;

import com.mceil.common.mapper.BaseMapper;
import com.mceil.user.pojo.UserRole;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Update;

public interface UserRoleMapper extends BaseMapper<UserRole> {
}
