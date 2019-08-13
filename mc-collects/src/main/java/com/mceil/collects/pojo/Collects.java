package com.mceil.collects.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_collects")
public class Collects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long goodsId;
    private Long userId;
    private Long cid3;
    private Date createTime;
    private String noteMessage;
    @Transient
    private String image;
    @Transient
    private String goodsSn;
    @Transient
    private String title;
    @Transient
    private String prices;
}

