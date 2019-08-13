package com.mceil.item.bo;

import com.mceil.item.pojo.Goods;
import com.mceil.item.pojo.GoodsDetail;

import javax.persistence.Transient;
import java.util.List;

public class GoodsBo extends Goods {
    /**
     * 商品分类名称
     */
    @Transient
    private String cname;
    /**
     * 品牌名称
     */
    @Transient
    private String bname;


    /**
     * 商品详情
     */
    @Transient
    private GoodsDetail goodsDetail;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public GoodsDetail getGoodsDetail() {
        return goodsDetail;
    }

    public void setGoodsDetail(GoodsDetail goodsDetail) {
        this.goodsDetail = goodsDetail;
    }
}
