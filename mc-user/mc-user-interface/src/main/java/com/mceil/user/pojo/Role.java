package com.mceil.user.pojo;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

@Data
@Table(name = "tb_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //名称
    private String name;

   //描述
    private String description;

   //后台用户数量
    private Integer adminCount;

   //创建时间
    private Date createTime;

   //启用状态：0->禁用；1->启用
    private Integer status;

    private Integer sort;

}