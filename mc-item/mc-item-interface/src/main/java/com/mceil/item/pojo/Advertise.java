package com.mceil.item.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_advertise")
public class Advertise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int type;
    private String pic;
    private Date startTime;
    private Date endTime;
    private Boolean status;
    private int clickCount;
    private int orderCount;
    private String url;
    private String note;
    private int sort;
}
