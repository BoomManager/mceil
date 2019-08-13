package com.mceil.cart.service.impl;

import com.mceil.auth.pojo.UserInfo;
import com.mceil.cart.client.BrandClient;
import com.mceil.cart.client.GoodsClient;
import com.mceil.cart.interceptor.UserInterceptor;
import com.mceil.cart.pojo.Cart;
import com.mceil.cart.service.CartService;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.utils.JsonUtils;
import com.mceil.item.api.GoodsApi;
import com.mceil.item.bo.GoodsBo;

import com.mceil.item.bo.PriceBo;
import com.mceil.item.pojo.Brand;
import com.mceil.item.pojo.Goods;
import com.mceil.item.pojo.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX="cart:uid:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;

    @Override
    public List<Cart> queryCartListByRedis(UserInfo user) {
        String key = KEY_PREFIX + user.getId();
        if (!redisTemplate.hasKey(key)){
            //key不存在，返回404
            //throw new McException(ExceptionEnum.CART_NOT_FOUND);
            return null;
        }
        //获取登录用户的所有购物车
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> carts = operations.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }
    //合并本地和购物车中的数据
    @Override
    public List<Cart> mergeCartList(List<Cart> cartListRedis, List<Cart> cartList_cookie) {
        for (Cart cart_cookie : cartList_cookie) {
            for (Cart cart_redis : cartListRedis) {
                cartListRedis = addGoodToCartList(cartListRedis,cart_cookie.getGoodsId(),cart_cookie.getNum());
            }
        }
        return cartListRedis;
    }

    @Override
    public List<Cart> addGoodToCartList(List<Cart> cartList, Long goodsId, Integer num) {
        //获取商品信息
        GoodsBo goods = goodsClient.queryGoodsById(goodsId).getBody();
        if(goods == null){
            throw new McException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //获取品牌信息
        Brand brand = brandClient.queryBrandById(goods.getBrandId()).getBody();
        //获取库存信息
        Stock stock = goodsClient.queryStockById(goodsId).getBody();
        List<Long> ids = new ArrayList<>();
        if(!CollectionUtils.isEmpty(cartList)){
            for (Cart cart : cartList) {
                ids.add(cart.getGoodsId());
            }
        }else {
            //避免抛空指针异常
            cartList = new ArrayList<>();
        }

        if(ids.contains(goodsId)){
            //购物车中有数据
            for (Cart cart : cartList) {
                if(cart.getGoodsId().longValue() == goodsId.longValue()){
                    List<PriceBo> priceBos = JsonUtils.toList(goods.getPrices(),PriceBo.class);
                    cart.setNum(cart.getNum() + num);
                    cart.setPrice(getGoodsPrice(priceBos,cart.getNum()));
                }
            }
        }else{
            //新增购物车数据
            Cart cart = new Cart();
            cart.setGoodsId(goodsId);
            cart.setNum(num);
            cart.setImage(goods.getImages());
            cart.setOwnSpec(goods.getOwnSpec());
            String prices = goods.getPrices();
            List<PriceBo> priceBos = JsonUtils.toList(prices,PriceBo.class);
            cart.setPrice(getGoodsPrice(priceBos,num));
            cart.setTitle(goods.getTitle());
            cart.setBname(brand.getName());
            cart.setPrices(goods.getPrices());
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setStock(stock.getStock());
            cart.setUnit(goods.getUnit());
            cart.setUnitNum(goods.getUnitNum());
            cartList.add(cart);
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(UserInfo user, List<Cart> cartList) {
        //key
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        for (Cart cart : cartList) {
            //hashkey
            String hashkey = cart.getGoodsId().toString();
            //写回redis
            operations.put(hashkey,JsonUtils.toString(cart));
        }
    }

    @Override
    public List<Cart> updateCartNum(List<Cart> list, Long goodsId, Integer num) {
        if(!CollectionUtils.isEmpty(list)){
            for (Cart cart : list) {
                if(cart.getGoodsId().longValue() == goodsId){
                    cart.setNum(num);
                }
            }
        }
        return list;
    }

    @Override
    public void updateCartNumForRedis(Long userId, Long goodsId, Integer num) {

        String key = KEY_PREFIX + userId;
        BoundHashOperations<String,Object,Object> hashOperations = this.stringRedisTemplate.boundHashOps(key);
        //2.获取购物车
        String json = hashOperations.get(goodsId.toString()).toString();
        Cart cart = JsonUtils.parse(json,Cart.class);
        cart.setNum(num);
        //3.写入购物车
        hashOperations.put(goodsId.toString(),JsonUtils.serialize(cart));
    }



    @Override
    public List<Cart> deleteCartCookie(List<Cart> list,List<Long> ids) {
        for (Long id : ids) {
            list = removeCart(list,id);
        }
        return list;
    }

    private List<Cart> removeCart(List<Cart> list, Long id) {
        if(!CollectionUtils.isEmpty(list)){
            for (Cart cart : list) {
                if(cart.getGoodsId().longValue() == id){
                    list.remove(cart);
                }
            }
        }
        return list;
    }

    @Override
    public void deleteCartForRedis(UserInfo user, List<Long> ids) {
        for (Long id : ids) {
            //key
            String key = KEY_PREFIX + user.getId();

            //删除操作
            redisTemplate.opsForHash().delete(key,id.toString());
        }

    }

    @Override
    public UserInfo getCurrentUser() {
        return UserInterceptor.getUser();
    }

    //获取商品单价
    private Long getGoodsPrice(List<PriceBo> priceBos, Integer num) {
        Long price = 0L;
        if(!CollectionUtils.isEmpty(priceBos)){
            for (int i = 0; i < priceBos.size(); i++){
                PriceBo priceBo = priceBos.get(i);
                //如果传进来的数量大于价格几何的最大数量，则取最高的价格
                if(i == priceBos.size() - 1){
                    price = priceBo.getPrice();
                }
                //获取最接近的价格
                if(priceBo.getCount() > num){
                    if(i != 0){
                        price = priceBos.get(i-1).getPrice();
                        //跳出循环
                        break;
                    }else {
                        //取第一个价格
                        price = priceBo.getPrice();
                        break;
                    }

                }
            }
        }
        return price;
    }
}
