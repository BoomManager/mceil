package com.mceil.user.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//用户商品收藏
@Data
@Table(name="tb_collections")
public class Collections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "产品id")
    private Long productId;

    @ApiModelProperty(value = "会员id")
    private Long memeberId;
}
