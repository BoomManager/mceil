package com.mceil.item.bo;

import com.mceil.item.pojo.Brand;
import lombok.Data;

import javax.persistence.Transient;
@Data
public class BrandBo extends Brand {
    @Transient
    private Long cid1;
    @Transient
    private Long cid2;
    @Transient
    private Long cid3;
    @Transient
    private String cname;
}
