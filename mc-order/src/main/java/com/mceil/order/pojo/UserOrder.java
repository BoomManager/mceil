package com.mceil.order.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_order_user")
public class UserOrder {
    @Id
    private Long id;
    private Long goodsId;
    private Long orderId;
    private Long uid;
}
