package com.mceil.user.pojo;



import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_login_log")
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Date createTime;

    private String ip;

    private String city;

   //登录类型：0->PC；1->android;2->ios;3->小程序
    private Integer loginType;

    private String province;

}