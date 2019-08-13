package com.mceil.item.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="tb_spec")
public class Spec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    @Column(name = "spec_template")
    private String specTemplate;
    @Transient
    private String cname;
}
