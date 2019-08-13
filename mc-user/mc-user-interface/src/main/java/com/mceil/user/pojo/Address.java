package com.mceil.user.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "tb_address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ApiModelProperty(value = "收货人名称")
    private String name;

    private String phone;

    @ApiModelProperty(value = "是否为默认")
    @Column(name = "default_address")
    private Integer defaultStatus;

    @ApiModelProperty(value = "邮政编码")
    private String zipCode;

    @ApiModelProperty(value = "省份/直辖市")
    private String state;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详细地址(街道)")
    private String address;

    private String label;

}
