package com.mceil.item.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Table(name="tb_goods_detail")
public class GoodsDetail implements Serializable {
    @Id
    private Long goodsId;// 对应的SPU的id
    private BigDecimal weight;  //商品毛重
    private String description;// 商品描述
    private String spec;// 商品特殊规格的名称及可选值模板
    private String packingList;// 包装清单
    private String afterService;// 售后服务
}