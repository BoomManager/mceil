package com.mceil.order.service;

import com.mceil.common.utils.IdWorker;
import com.mceil.order.mapper.OrderDetailMapper;
import com.mceil.order.mapper.UserOrderMapper;
import com.mceil.order.pojo.Order;
import com.mceil.order.pojo.OrderDetail;
import com.mceil.order.pojo.UserOrder;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserOrderMapper userOrderMapper;
    @Test
    public void getId(){
        Long id = 1156782677479788544L;
        System.out.println("获取到的id"+id);

        Order order = new Order();
        order.setOrderId(id);
        System.out.println("获取到order的id"+order.getOrderId());
    }
//    @Test
//    public void queryOrderByUid() {
//        orderService.queryOrderByUid(29L);
//    }
/*    @Test
    public void updateUserOrder(){
        List<OrderDetail> orderDetails = orderDetailMapper.selectAll();
        List<UserOrder> userOrderList =new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            UserOrder userOrder = new UserOrder();
            userOrder.setUid(29L);
            userOrder.setSkuid(orderDetail.getSkuId());
            userOrder.setOrderid(orderDetail.getOrderId());
            userOrderList.add(userOrder);
        }
        userOrderMapper.insertList(userOrderList);
    }*/
}