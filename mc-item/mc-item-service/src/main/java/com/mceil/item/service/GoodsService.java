package com.mceil.item.service;

import com.mceil.common.dto.CartDTO;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.GoodsBo;
import com.mceil.item.pojo.Goods;
import com.mceil.item.pojo.GoodsDetail;
import com.mceil.item.pojo.Stock;

import java.util.List;

public interface GoodsService {


    PageResult<Goods> queryGoodsByPage(Integer page, Integer rows, Boolean saleable, String keyword,String goodsSn);

    void saveGoods(GoodsBo goodsBo);

    void updateGoods(GoodsBo goodsBo);

    GoodsBo queryGoodsById(Long id);

    GoodsDetail queryGoodsDetailById(Long id);

    void updateGoodsNewStatusBatch(List<Long> ids,int status);

    void updateGoodsRecommendBatch(List<Long> ids,int status);


    void GoodsSoldOut(Long id);

    void deleteGoods(List<Long> ids);

    PageResult<Goods> queryGoodsNewStatus(Integer page, Integer rows);

    PageResult<Goods> queryGoodsRecommend(Integer page, Integer rows);

    void updateGoodsNewStatus(Long id);

    void updateGoodsRecommend(Long id);

    void updateCheckStatus(Long id, int status);

    List<Goods> queryGoodsByIds(List<Long> ids);

    void decreaseStock(List<CartDTO> carts);

    PageResult<Goods> queryGoodsByPageIndex(Integer page, Integer rows, String key, Long cid);

    Stock queryStockById(Long id);
}
