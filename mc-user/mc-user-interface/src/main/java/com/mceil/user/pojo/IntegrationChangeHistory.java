package com.mceil.user.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "tb_integration_change_history")
//积分记录
public class IntegrationChangeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Date createTime;

    //改变类型：0->增加；1->减少
    private Integer changeType;

    //积分改变数量
    private Integer changeCount;

    //操作人员
    private String operateMan;

    //操作备注
    private String operateNote;

    //积分来源：0->购物；1->管理员修改
    private Integer sourceType;

}