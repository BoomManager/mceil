package com.mceil.user.pojo;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //父级权限id
    private Long pid;

   //名称
    private String name;

    //权限值
    private String value;

    //图标
    private String icon;

    //权限类型：0->目录；1->菜单；2->按钮（接口绑定权限）
    private Integer type;

   //前端资源路径
    private String uri;

    //启用状态；0->禁用；1->启用
    private Integer status;

    //创建时间
    private Date createTime;

    //排序
    private Integer sort;

}