package com.mceil.user.mapper;



import com.mceil.user.pojo.Address;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AddressMapper extends Mapper<Address> {
    @Update("update tb_address set default_address = 1 where id = #{id}")
    void updateDefaultAddress(Long id);
}
