package com.mceil.item.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spec_value")
public class SpecValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long specId;
    private String spec;
    private String specValue;
    private String specAlias;
}
