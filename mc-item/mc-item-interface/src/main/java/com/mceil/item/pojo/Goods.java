package com.mceil.item.pojo;

import com.mceil.item.bo.PriceBo;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Table(name="tb_goods")
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private Long brandId;
    private String goodsSn;
    private String title;
    private String subTitle;
    private String images;
    private String goodsModel;
    private String material;
    private String prices;
    private String unit;
    @Column(name = "unit_num")
    private Integer unitNum;
    @Column(name = "new_status")
    private Boolean newStatus;
    @Column(name = "recommand_status")
    private Boolean recommandStatus;
    private Boolean saleable;
    @Column(name = "own_spec")
    private String ownSpec;
    private Boolean isDelete;
    @Column(name = "check_status")
    private Integer checkStatus;
    private Integer sale;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;

    @Transient
    private Stock stock;
    @Transient
    private String bname;
    @Transient
    private String cname;

}
