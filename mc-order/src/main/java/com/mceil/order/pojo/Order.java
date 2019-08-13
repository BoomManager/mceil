package com.mceil.order.pojo;


import lombok.Data;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_order")
public class Order {

    @Id
    @Column(name = "order_id")
    private Long orderId;// id

    private Long totalPay;// 总金额

    private Long actualPay;// 实付金额

    @Column(name = "payment_type")
    private Integer paymentType; // 支付类型，1、在线支付，2、货到付款

    @Column(name = "promotion_ids")
    private String promotionIds; // 参与促销活动的id

    @Column(name = "post_fee")
    private Long postFee = 0L;// 邮费

    @Column(name = "create_time")
    private Date createTime;// 创建时间

    @Column(name = "shipping_name")
    private String shippingName;// 物流名称

    @Column(name = "shipping_code")
    private String shippingCode;// 物流单号

    @Column(name = "user_id")
    private Long userId;// 用户id

    @Column(name = "buyer_message")
    private String buyerMessage;// 买家留言

    @Column(name = "buyer_nick")
    private String buyerNick;// 买家昵称

    @Column(name = "buyer_rate")
    private Boolean buyerRate;// 买家是否已经评价

    private String receiver; // 收货人全名

    @Column(name = "receiver_mobile")
    private String receiverMobile; // 移动电话

    @Column(name = "receiver_state")
    private String receiverState; // 省份

    @Column(name = "receiver_city")
    private String receiverCity; // 城市

    @Column(name = "receiver_district")
    private String receiverDistrict; // 区/县

    @Column(name = "receiver_address")
    private String receiverAddress; // 收货地址，如：xx路xx号

    @Column(name = "receiver_zip")
    private String receiverZip; // 邮政编码,如：310001

    @Column(name = "invoice_type")
    private Integer invoiceType = 0;// 发票类型，0无发票，1普通发票，2电子发票，3增值税发票

    @Column(name = "source_type")
    private Integer sourceType = 1;// 订单来源 1:app端，2：pc端，3：M端，4：微信端，5：手机qq端

    @Transient
    private OrderStatus orderStatus;

    @Transient
    private List<OrderDetail> orderDetails;

    @Transient
    private Integer totalNum;
}