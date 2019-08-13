package com.mceil.cart.mq;

import com.mceil.auth.pojo.UserInfo;
import com.mceil.cart.interceptor.UserInterceptor;
import com.mceil.cart.service.CartService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CartListener {
    @Autowired
    private CartService cartService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.goods.delete.queue",durable = "true"),
            exchange = @Exchange(name = "mc.cart.exchange",type = ExchangeTypes.TOPIC),
            key = {"cart.delete"}
    ))
    public void ListenCreateOrder(Map<Long,Object> cart) throws IOException {
        Long userId = 0L;
        List<Long> ids = new ArrayList<>();
        for (Long key : cart.keySet()){
            userId = key;
            ids = (List<Long>) cart.get(key);
        }
        UserInfo user = new UserInfo();
        user.setId(userId);
        if (CollectionUtils.isEmpty(ids)){
            return;
        }
        //处理消息，对索引库进行新增或修改
        cartService.deleteCartForRedis(user,ids);
    }

}
