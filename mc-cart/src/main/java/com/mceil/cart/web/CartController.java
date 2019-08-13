package com.mceil.cart.web;

import com.alibaba.fastjson.JSON;
import com.mceil.auth.pojo.UserInfo;
import com.mceil.cart.interceptor.UserInterceptor;
import com.mceil.cart.pojo.Cart;
import com.mceil.cart.pojo.UserTest;
import com.mceil.cart.service.CartService;
import com.mceil.common.utils.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
@Api("购物车接口")
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

   /* *//**
     * 新增购物车
     * @param cart
     * @return
     *//*
    @ApiOperation(value = "接收前端传来的购物车数据,创建购物车记录")
    @ApiImplicitParam(name = "cart", required = true, value = "购物车的json对象")
    @ApiResponse(code = 201, message = "购物车车成功创建,且无返回值" )
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    *//**
     * 查询购物车
     * @return
     *//*
    @ApiOperation(value = "查询购物车的列表(前提登陆用户后)")
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(){
        return ResponseEntity.ok(cartService.queryCartList());
    }

    *//**
     * 修改购物车的商品数量
     * @param skuId
     * @param num
     * @return
     *//*
    @PutMapping
    @ApiOperation(value = "修改购物车的商品数量(前提登陆用户后)")
    @ApiResponse(code = 204, message = "修改成功，且无返回值")
    public ResponseEntity<Void> updateCartNum(
            @RequestParam("id") Long skuId,
            @RequestParam("num") Integer num){
        cartService.updateCartNum(skuId,num);
     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    *//**
     * 删除购物车数据

     * @return
     *//*
    @ApiOperation(value = "删除购物车的单个商品(前提登陆用户后)")
    @ApiImplicitParam(name = "{skuId}", required = true, value = "商品集子集(skuId)")
    @ApiResponse(code = 204, message = "删除选定商品成功,且无返回值")
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }*/
    @ApiOperation(value = "接收前端传来的购物车数据,创建购物车记录")
    @ApiResponse(code = 201, message = "购物车成功创建,且无返回值" )
    @PostMapping("add")

    public ResponseEntity<Void> addCart(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("goodsId") Long goodsId,
                                        @RequestParam("num")Integer num){
        //获取当前登录用户
        UserInfo user = cartService.getCurrentUser();

        //获取购物车列表数据
        List<Cart> cartList = queryCartList(request,response).getBody();

        //将数据添加到购物车列表中
        cartList = cartService.addGoodToCartList(cartList,goodsId,num);
        if (user == null){
            String cartListString = JSON.toJSONString(cartList);
            CookieUtils.setCookie(request, response, "cartList", cartListString, 3600*24, "UTF-8");
            System.out.println("向cookie存储购物车");
        }else{
            //将数据存放到Redis中
            cartService.saveCartListToRedis(user,cartList);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 查询购物车
     * @return
     */
    @ApiOperation(value = "查询购物车的列表(前提登陆用户后)")
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(HttpServletRequest request, HttpServletResponse response){
        //获取当前登录用户
        UserInfo user = cartService.getCurrentUser();

        //获取cookie中的数据
        String cartListString = CookieUtils.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }
        //cookie中的购物车数据
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if(user == null){
            //用户未登录
            //从cookie中提取购物车
            return ResponseEntity.ok(cartList_cookie);
        }else{
            //用户已登录
            List<Cart> cartListRedis = cartService.queryCartListByRedis(user);
            if(!CollectionUtils.isEmpty(cartList_cookie)){
                //判断当本地购物车中存在数据
                //合并缓存和cookie中的数据
                List<Cart> cartList = cartService.mergeCartList(cartListRedis,cartList_cookie);
                cartService.saveCartListToRedis(user,cartList);
                //本地购物车清除
                CookieUtils.deleteCookie(request, response, "cartList");
                return ResponseEntity.ok(cartList);
            }else {
                //返回Redis中的购物车数据
                return ResponseEntity.ok(cartListRedis);
            }
        }
    }

    @PostMapping("update")
    @ApiOperation(value = "修改购物车的商品数量(前提登陆用户后)")
    @ApiResponse(code = 204, message = "修改成功，且无返回值")
    public ResponseEntity<Void> updateCartNum(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam("goodsId") Long goodsId,
            @RequestParam("num") Integer num){
        //获取当前登录用户
        UserInfo user = cartService.getCurrentUser();

        //获取购物车列表数据
        List<Cart> list = queryCartList(request,response).getBody();
        list = cartService.updateCartNum(list,goodsId,num);

        if(user == null){
            String cartListString = JSON.toJSONString(list);
            CookieUtils.setCookie(request, response, "cartList", cartListString, 3600*24, "UTF-8");
            System.out.println("向cookie存储购物车");
        }else{
            cartService.updateCartNumForRedis(user.getId(),goodsId,num);

        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @ApiOperation(value = "删除购物车的单个商品(前提登陆用户后)")
    @ApiResponse(code = 204, message = "删除选定商品成功,且无返回值")
    @PostMapping("delete/{ids}")
    public ResponseEntity<Void> deleteCart(HttpServletRequest request, HttpServletResponse response,
                                           @PathVariable("ids") List<Long> ids){
        //获取当前登录用户
        UserInfo user = cartService.getCurrentUser();

        //获取购物车列表数据
        List<Cart> list = queryCartList(request,response).getBody();
        //删除数据
        if(user == null){
            //删除cookie中购物车对应的数据
            list = cartService.deleteCartCookie(list,ids);
            String cartListString = JSON.toJSONString(list);
            //将数据添加到cookie中
            CookieUtils.setCookie(request, response, "cartList", cartListString, 3600*24, "UTF-8");

        }else{
            cartService.deleteCartForRedis(user,ids);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
