package com.mceil.user.mapper;

import com.mceil.user.pojo.OrderPlaceContact;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderPlaceContactMapper extends Mapper<OrderPlaceContact> {
    @Select("select * from oms_order_place_contact where member_id = #{memberId}")
    List<OrderPlaceContact> getOrderPlaceContactByMemberId(Long memberId);
    @Update("update oms_order_place_contact set status = 0")
    int updateOrderPlaceContactStatus();
}
