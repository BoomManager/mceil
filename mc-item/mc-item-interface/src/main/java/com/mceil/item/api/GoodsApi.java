package com.mceil.item.api;

import com.mceil.common.dto.CartDTO;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.GoodsBo;
import com.mceil.item.pojo.Goods;
import com.mceil.item.pojo.GoodsDetail;
import com.mceil.item.pojo.Stock;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("goods")
public interface GoodsApi {
  /*  *//**
     * 根据spu的id查询详情detail
     * @param spuId
     * @return
     *//*
    @GetMapping("/spu/detail/{id}")
    GoodsDetail queryDetailById(@PathVariable("id") Long spuId);

    *//**
     * 根据spu查询下面的所有sku
     * @param spuId
     * @return
     *//*
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);

    *//**
     * 分页查询Spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     *//*
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "key",required = false) String key
    );

    *//**
     * 根据spu的id查询spu
     * @param id
     * @return
     *//*
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);

    *//**
     * 根据id批量查询sku
     * @param ids
     * @return
     *//*
    @GetMapping("sku/list/ids")
    List<Sku> querySkuByIds(@RequestParam("ids") List<Long> ids);

    *//**
     * 减库存
     * @param carts
     * @return
     *//*
    @PostMapping("stock/decrease")
    Void decreaseStock(@RequestBody List<CartDTO> carts);*/

    /**
     * 根据商品id获取商品详情
     * @param id
     * @return
     */
    @GetMapping("{id}")
    ResponseEntity<GoodsBo> queryGoodsById(@PathVariable Long id);
    @GetMapping("detail/{id}")
    GoodsDetail queryDetailById(@PathVariable Long id);
    @GetMapping("/page")
    ResponseEntity<PageResult<Goods>> queryGoodsByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "goodsSn", required = false) String goodsSn);

    /**
     * 根据商品ids获取商品列表
     * @param ids
     * @return
     */
    @GetMapping("order")
    ResponseEntity<List<Goods>> queryGoodsByIds(@RequestParam("ids") List<Long> ids);

    /**
    * 减库存
    * @param carts
    * @return
    */
    @PostMapping("stock/decrease")
    Void decreaseStock(@RequestBody List<CartDTO> carts);

    @ApiOperation(value = "分类id获取商品列表")
    @GetMapping("/stock/{id}")
    ResponseEntity<Stock> queryStockById(
            @PathVariable("id") Long id);
}
