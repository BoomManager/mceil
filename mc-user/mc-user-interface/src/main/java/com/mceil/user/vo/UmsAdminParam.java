package com.mceil.user.vo;


import lombok.Data;
/**
 * 用户登录参数
 */
@Data
public class UmsAdminParam {

    private String username;

    private String password;

    private String icon;

    private String email;

    private String nickName;

    private String note;
}
