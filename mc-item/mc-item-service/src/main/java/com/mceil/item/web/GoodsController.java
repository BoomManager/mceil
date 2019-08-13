package com.mceil.item.web;

import com.mceil.common.dto.CartDTO;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.GoodsBo;
import com.mceil.item.bo.GoodsQueryByPageParameter;
import com.mceil.item.pojo.Goods;
import com.mceil.item.pojo.GoodsDetail;
import com.mceil.item.pojo.Stock;
import com.mceil.item.service.GoodsService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("商品接口")
@RestController
@RequestMapping("goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;




    @ApiOperation(value = "分页获取商品列表")
    @GetMapping("/page")
    public ResponseEntity<PageResult<Goods>> queryGoodsByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "goodsSn", required = false) String goodsSn) {
        return ResponseEntity.ok(goodsService.queryGoodsByPage(page, rows, saleable, keyword,goodsSn));
    }
/*     @ApiOperation(value = "分页获取商品列表")
     @GetMapping("/spu/page")
     public ResponseEntity<PageResult<Goods>> querySpuByPage(GoodsQueryByPageParameter queryByPageParameter) {
         return ResponseEntity.ok(goodsService.queryGoodsByPage(queryByPageParameter.getPage(), queryByPageParameter.getRows(), queryByPageParameter.getSaleable(),
                 queryByPageParameter.getKey(), queryByPageParameter.getCid(),queryByPageParameter.getBid(),queryByPageParameter.getCheckStatus(),queryByPageParameter.getGoodsSn()));
     }*/
    /**
     * 新增商品
     * @param goodsBo
     * @return
     */
    @ApiOperation(value = "保存商品")
    @PostMapping()
    public ResponseEntity<Void> saveGoods(@RequestBody GoodsBo goodsBo){
        this.goodsService.saveGoods(goodsBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 更新商品
     * @param goodsBo
     * @return
     */
    @ApiOperation(value = "更新商品")
    @PutMapping()
    public ResponseEntity<Void> updateGoods(@RequestBody GoodsBo goodsBo){
        this.goodsService.updateGoods(goodsBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //根据商品id查询商品
    @ApiOperation(value = "根据商品id查询商品")
    @GetMapping("{id}")
    public ResponseEntity<GoodsBo> queryGoodsById(@PathVariable Long id){
        GoodsBo goodsBo = this.goodsService.queryGoodsById(id);
        if (goodsBo == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(goodsBo);
    }
/*    @ApiOperation(value = "根据商品id查询商品详情")
    @GetMapping("/detail/{id}")
    public ResponseEntity<GoodsDetail> queryDetailById(@PathVariable Long id){
        GoodsDetail goodsDetail = this.goodsService.queryGoodsDetailById(id);
        if (goodsDetail == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            return ResponseEntity.ok(goodsDetail);
        }
    }*/
    @ApiOperation(value = "商品批量上下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "商品分类id集合")})
    @PostMapping("/out/{id}")
    public ResponseEntity<Void> GoodsSoldOutBacth(@PathVariable("id") String ids){
        String separator="-";
        if(ids.contains(separator)){
            String[] goodsId = ids.split(separator);
            for (String goodId : goodsId) {
                this.goodsService.GoodsSoldOut(Long.parseLong(goodId));
            }
        }else {
            this.goodsService.GoodsSoldOut(Long.parseLong(ids));
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "批量更新商品的新品状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "商品分类id集合"),
            @ApiImplicitParam(name = "status", required = true, value = "1表示批量新品，2表示批量隐藏新品")})
    @PutMapping("/new")
    public ResponseEntity<Void> updateGoodsNewStatusBatch(@RequestParam("ids") List<Long> ids,int status){
        this.goodsService.updateGoodsNewStatusBatch(ids,status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation(value = "更新商品的新品状态")
    @PutMapping("/newStatus/{id}")
    public ResponseEntity<Void> updateGoodsNewStatus(@PathVariable("id") Long id){
        this.goodsService.updateGoodsNewStatus(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "更新商品的推荐状态")
    @PutMapping("/newRecommend/{id}")
    public ResponseEntity<Void> updateGoodsRecommend(@PathVariable("id") Long id){
        this.goodsService.updateGoodsRecommend(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation(value = "商品审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "商品分类id"),
            @ApiImplicitParam(name = "status", required = true, value = "0表示未审核，1表示审核通过，2表示审核失败")})
    @PutMapping("/check")
    public ResponseEntity<Void> updateCheckStatus(@RequestParam("id") Long id,int status){
        this.goodsService.updateCheckStatus(id,status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation(value = "批量更新商品推荐状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "商品分类id集合"),
            @ApiImplicitParam(name = "status", required = true, value = "1表示批量上架，2表示批量下架")})
    @PutMapping("/recommend")
    public ResponseEntity<Void> updateGoodsRecommendBatch(@RequestParam("ids") List<Long> ids,int status){
        this.goodsService.updateGoodsRecommendBatch(ids,status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation(value = "批量删除商品")
    @DeleteMapping("{ids}")
    public ResponseEntity<Void> deleteGoods(@PathVariable List<Long> ids){
        this.goodsService.deleteGoods(ids);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "热门商品，提供给前台调用")
    @PostMapping("/home/new")
    public ResponseEntity<PageResult<Goods>> queryGoodsNewStatus(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "rows", defaultValue = "5") Integer rows){
        PageResult<Goods> list = this.goodsService.queryGoodsNewStatus(page,rows);
        if(!CollectionUtils.isEmpty(list.getItems())){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }
    @ApiOperation(value = "推荐商品，提供给前台调用")
    @PostMapping("/home/recommend")
    public ResponseEntity<PageResult<Goods>> queryGoodsRecommend(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "rows", defaultValue = "5") Integer rows){
        PageResult<Goods> list = this.goodsService.queryGoodsRecommend(page,rows);
        if(!CollectionUtils.isEmpty(list.getItems())){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "根据ids获取商品信息，提供接口给订单微服务调用")
    @GetMapping("order")
    public ResponseEntity<List<Goods>> queryGoodsByIds(@RequestParam("ids") List<Long> ids){
        List<Goods> list = goodsService.queryGoodsByIds(ids);
        if(CollectionUtils.isEmpty(list)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "根据购物车集合减库存")
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> carts){
        goodsService.decreaseStock(carts);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "分类id获取商品列表")
    @GetMapping("/index/page")
    public ResponseEntity<PageResult<Goods>> queryGoodsPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "cid") Long cid) {
        return ResponseEntity.ok(goodsService.queryGoodsByPageIndex(page, rows,key,cid));
    }
    @ApiOperation(value = "获取商品库存")
    @GetMapping("/stock/{id}")
    public ResponseEntity<Stock> queryStockById(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.queryStockById(id));
    }

}
