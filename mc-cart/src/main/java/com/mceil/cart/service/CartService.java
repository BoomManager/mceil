package com.mceil.cart.service;

import com.mceil.auth.pojo.UserInfo;
import com.mceil.cart.interceptor.UserInterceptor;
import com.mceil.cart.pojo.Cart;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSInput;

import java.util.List;
import java.util.stream.Collectors;


public interface CartService {

    List<Cart> queryCartListByRedis(UserInfo user);

    List<Cart> mergeCartList(List<Cart> cartListRedis, List<Cart> cartList_cookie);

    List<Cart> addGoodToCartList(List<Cart> cartList,Long goodsId,Integer num);

    void saveCartListToRedis(UserInfo user, List<Cart> cartList);

    List<Cart> updateCartNum(List<Cart> list, Long goodsId, Integer num);

    void updateCartNumForRedis(Long userId,Long goodsId,Integer num);


    List<Cart> deleteCartCookie(List<Cart> list,List<Long> ids);

    void deleteCartForRedis(UserInfo user, List<Long> ids);
    //获取当前用户
    UserInfo getCurrentUser();


/*
    public void addCart(Cart cart) {
        //获取当前登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //hashkey
        String hashkey = cart.getSkuId().toString();
        //记录购物车中的num
        Integer num = cart.getNum();

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //判断当前商品是否存在
        if (operations.hasKey(hashkey)) {
            //存在，修改数量
            String cartJson = operations.get(hashkey).toString();
            cart = JsonUtils.toBean(cartJson, Cart.class);
            cart.setNum(cart.getNum()+num);
        }
        //否,新增
        //写回redis
        operations.put(hashkey,JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList() {
        //获取当前登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        if (!redisTemplate.hasKey(key)){
            //key不存在，返回404
            throw new McException(ExceptionEnum.CART_NOT_FOUND);
        }
        //获取登录用户的所有购物车
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> carts = operations.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;

    }

    public void updateCartNum(Long skuId, Integer num) {
        //获取当前登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //hashKey
        String hashkey = skuId.toString();
        //获取操作
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        if (!operations.hasKey(skuId.toString())){
            throw new McException(ExceptionEnum.CART_NOT_FOUND);
        }
        //查询
        Cart cart = JsonUtils.toBean(operations.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);
        //写回redis
        operations.put(hashkey,JsonUtils.toString(cart));
    }

    public void deleteCart(Long skuId) {
        //获取当前登录用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();

        //删除操作
        redisTemplate.opsForHash().delete(key,skuId.toString());
    }*/

}
