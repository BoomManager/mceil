package com.mceil.order.mapper;

import com.mceil.common.mapper.BaseMapper;

import com.mceil.order.pojo.Order;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;


public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT * FROM tb_order  where user_id = #{userId} order by create_time desc limit #{page},#{rows}")
    List<Order> queryOrderListPage(Long userId,Integer page,Integer rows);

    @Select("SELECT * FROM tb_order  where user_id = #{userId} and order_id in #{ids} order by create_time desc limit #{page},#{rows}")
    List<Order> queryOrderListPageByIds(Long id, Set<Long> ids);
}
