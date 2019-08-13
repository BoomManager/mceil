package com.mceil.cart.pojo;

import lombok.Data;

@Data
public class Cart {
    private Long goodsId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
    private String prices;  //价格梯度
    private String goodsSn;   //商品编号
    private Integer stock;      //商品库存
    private String bname;   //品牌名称
    private String unit;    //单位
    private Integer unitNum;    //单位个数
}