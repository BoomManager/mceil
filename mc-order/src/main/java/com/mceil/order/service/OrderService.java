package com.mceil.order.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.auth.pojo.UserInfo;
import com.mceil.common.dto.CartDTO;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.utils.IdWorker;
import com.mceil.common.utils.JsonUtils;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.PriceBo;
import com.mceil.item.pojo.Goods;
import com.mceil.order.client.AddressClient;
import com.mceil.order.client.GoodsClient;

import com.mceil.order.dto.OrderDTO;
import com.mceil.order.dto.PageBean;
import com.mceil.order.enums.OrderStatusEnum;
import com.mceil.order.enums.PayState;
import com.mceil.order.interceptors.UserInterceptor;
import com.mceil.order.mapper.OrderDetailMapper;
import com.mceil.order.mapper.OrderMapper;
import com.mceil.order.mapper.OrderStatusMapper;
import com.mceil.order.mapper.UserOrderMapper;
import com.mceil.order.pojo.Order;
import com.mceil.order.pojo.OrderDetail;
import com.mceil.order.pojo.OrderStatus;
import com.mceil.order.pojo.UserOrder;
import com.mceil.order.utils.PayHelper;
import com.mceil.user.pojo.Address;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;



import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private UserOrderMapper userOrderMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PayHelper payHelper;
    @Autowired
    private AddressClient addressClient;
/*    @Autowired
    AlipayClient alipayClient;*/
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //1.新增订单
        Order order = new Order();
        //1.1订单编号，基本信息
        Long orderId = idWorker.nextId();
        String ss = orderId.toString();
        Long orderIdNew = Long.parseLong(ss.substring(0,15));
        order.setOrderId(orderIdNew);
        System.out.println(order.getOrderId());
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        //1.2用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);  //设置是否评价

        //1.3收货人信息
        //获取收货人信息
        Address addr = addressClient.findById(orderDTO.getAddressId()).getBody();
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());

        //1.4金额
        //把CartDTO转为一个map,key是goods的id,值是num
        Map<Long, Integer> numMap = orderDTO.getCarts().stream()
                .collect(Collectors.toMap(CartDTO::getGoodsId, CartDTO::getNum));
        //获取所有goods的id
        Set<Long> ids = numMap.keySet();


        //根据id查询goods
        List<Goods> goodsList = goodsClient.queryGoodsByIds(new ArrayList<>(ids)).getBody();

        //准备orderDetail的集合
        List<OrderDetail> details = new ArrayList<>();

        //准备userOrder的集合
        List<UserOrder> userOrderList = new ArrayList<>();
        //long类型做乘法运算精度会缺失，后期要改成BigDecimal
        long totalPay = 0L;
        List<Long> goodsIds = new ArrayList<>();
        for (Goods goods : goodsList) {
            //计算商品总价
            goodsIds.add(goods.getId());
            String prices = goods.getPrices();
            //将prices转成List<PriceBo>
            List<PriceBo> priceBos = JsonUtils.toList(prices,PriceBo.class);
            Long price = getGoodsPrice(priceBos,numMap.get(goods.getId()));

            totalPay += price * numMap.get(goods.getId());

            //封装orderdetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(goods.getImages(), ","));
            orderDetail.setNum(numMap.get(goods.getId()));
            orderDetail.setOrderId(orderIdNew);
            orderDetail.setOwnSpec(goods.getOwnSpec());
            orderDetail.setPrice(price);
            orderDetail.setGoodsId(goods.getId());
            orderDetail.setTitle(goods.getTitle());
            orderDetail.setGoodsSn(goods.getGoodsSn());
            details.add(orderDetail);

            //封装userorder
            UserOrder userOrder = new UserOrder();
            userOrder.setOrderId(orderIdNew);
            userOrder.setGoodsId(goods.getId());
            userOrder.setUid(user.getId());
            userOrderList.add(userOrder);

        }
        order.setTotalPay(totalPay);
        //实际金额: 总金额 + 邮费 - 优惠金额
        order.setActualPay(totalPay + order.getPostFee() - 0);

        //1.5 order写入数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            log.error("[创建订单] 创建订单失败 ，orderId:{}", orderId);
            throw new McException(ExceptionEnum.ORDER_CREATE_ERROR);
        }

        //2.新增订单详情
        count = orderDetailMapper.insertList(details);
        if (count != details.size()) {
            log.error("[创建订单] 创建订单失败 ，orderId:{}", orderId);
            throw new McException(ExceptionEnum.ORDER_CREATE_ERROR);
        }

        //2.1新增厂商和订单
        count = userOrderMapper.insertList(userOrderList);
        if (count != userOrderList.size()) {
            log.error("[创建订单] 创建订单失败,厂家信息未同步 ，orderId:{}", order.getOrderId());
            throw new McException(ExceptionEnum.ORDER_CREATE_ERROR);
        }

        //3.新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderIdNew);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1) {
            log.error("[创建订单] 创建订单失败 ，orderId:{}", orderId);
            throw new McException(ExceptionEnum.ORDER_CREATE_ERROR);
        }

        //4.减库存
        //应该是付款之后再减
/*        List<CartDTO> cartsDTOs = orderDTO.getCarts();
        goodsClient.decreaseStock(cartsDTOs);*/
        //发送消息队列删除购物车
        Map<Long,Object> cart = new HashMap<Long,Object>();
        cart.put(user.getId(),goodsIds);
        sendMessage(cart,"delete");
        return order.getOrderId();
    }
    //获取商品单价
    private Long getGoodsPrice(List<PriceBo> priceBos, Integer num) {
        Long price = 0L;
        if(!CollectionUtils.isEmpty(priceBos)){
            for (int i = 0; i < priceBos.size(); i++){
                PriceBo priceBo = priceBos.get(i);
                if(i == priceBos.size() - 1){
                    price = priceBo.getPrice();
                }
                //获取最接近的价格
                if(priceBo.getCount() > num){
                    if(i != 0){
                        price = priceBos.get(i-1).getPrice();
                        //跳出循环
                        break;
                    }else {
                        price = priceBo.getPrice();
                        break;
                    }

                }
            }
        }
        return price;
    }

    public Order queryOrderById(Long id) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(detail);
        if (CollectionUtils.isEmpty(details)) {
            throw new McException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(details);

        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus == null) {
            //不存在
            throw new McException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);

        return order;
    }

    public PageResult<Order> queryOrderByUid(Integer page, Integer rows,String keyword,Integer status,Long orderId) {
        //分页
        UserInfo user = UserInterceptor.getUser();
        Set<Long> ids = new HashSet<>();
        if(StringUtils.isNotBlank(keyword) && status != null){
            //模糊查询订单详情
            List<OrderDetail> orderDetailList = getOrderDetailList(orderId,keyword);
            //根据订单状态查询订单列表
            List<OrderStatus> orderStatusList = getOrderStatusList(orderId,status);
            //获取订单详情中的orderIds
            Set<Long> detailIds = orderDetailList.stream().map(OrderDetail :: getOrderId).collect(Collectors.toSet());
            //获取订单状态中的orderIds
            Set<Long> statusIds = orderStatusList.stream().map(OrderStatus :: getOrderId).collect(Collectors.toSet());
            //取两个集合的交集，只有相交的数据才符合搜索结果
            if(!CollectionUtils.isEmpty(detailIds) && !CollectionUtils.isEmpty(statusIds)){
                for (Long detailId : detailIds) {
                    if(statusIds.contains(detailId)){
                        ids.add(detailId);
                    }
                }
            }
        }else {
            if(StringUtils.isNotBlank(keyword)){
                //模糊查询订单详情
                List<OrderDetail> orderDetailList = getOrderDetailList(orderId,keyword);
                ids = orderDetailList.stream().map(OrderDetail :: getOrderId).collect(Collectors.toSet());
            }
            if(status != null){
                //根据订单状态查询订单列表
                List<OrderStatus> orderStatusList = getOrderStatusList(orderId,status);
                ids = orderStatusList.stream().map(OrderStatus :: getOrderId).collect(Collectors.toSet());
            }
        }
        PageHelper.startPage(page,rows);
        List<Order> orderList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(ids)){
            //根据ids和userId获取订单列表
            Example example = new Example(Order.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",user.getId()).andIn("orderId",ids);
            orderList = orderMapper.selectByExample(example);

        }else {
            if(StringUtils.isBlank(keyword) && status == null){
                //根据userId和orderId获取订单列表
                orderList = queryOrderList(user.getId(),orderId);
            }else if(status == 0){
                orderList = queryOrderList(user.getId(),orderId);
            }
        }
        //添加订单详情
        addStateDetail(orderList);
        //添加订单状态
        addOrderState(orderList);
        //解析分页结果
        PageInfo<Order> info = new PageInfo<>(orderList);
        return new PageResult<>(info.getTotal(), orderList);

    }
    public List<Order> queryOrderList(Long userId,Long orderId){
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if(orderId != null){
            criteria.andEqualTo("orderId",orderId);
        }
        return orderMapper.selectByExample(example);
    }
    private List<OrderStatus> getOrderStatusList(Long orderId,Integer status) {
        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        if(orderId != null){
            criteria.andEqualTo("orderId",orderId);
        }
        if(status != 0){
            criteria.andEqualTo("status",status);
        }
        return orderStatusMapper.selectByExample(example);
    }

    public List<OrderDetail> getOrderDetailList(Long orderId,String keyword){
        Example detailExample = new Example(OrderDetail.class);
        Example.Criteria deCriteria = detailExample.createCriteria();
        if(orderId != null){
            deCriteria.andEqualTo("orderId",orderId);
        }
        deCriteria.andLike("title","%" + keyword + "%").orLike("goodsSn","%" + keyword + "%");
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByExample(detailExample);
        return orderDetailList;
    }


    private void addOrderState(List<Order> orderList) {
        if(!CollectionUtils.isEmpty(orderList)){
            for (Order order : orderList) {
                OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(order.getOrderId());
                order.setOrderStatus(orderStatus);
            }
        }
    }

    private void addStateDetail(List<Order> orderList) {
        if(!CollectionUtils.isEmpty(orderList)){
            for (Order order : orderList) {
                Example example = new Example(OrderDetail.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("orderId",order.getOrderId());
                List<OrderDetail> orderDetailList = orderDetailMapper.selectByExample(example);
                order.setOrderDetails(orderDetailList);
            }
        }
    }


    //查询正常订单
    public List<Order> queryOrderRight() {
        UserInfo user = UserInterceptor.getUser();
        Example example = new Example(OrderStatus.class);
        example.createCriteria().andNotEqualTo("status", 7);
        List<Order> orderList = queryOrderListByExample(example);
        return orderList;
    }

    //查询异常订单
    public List<Order> queryOrderError() {
        Example example = new Example(OrderStatus.class);
        example.createCriteria().orEqualTo("status", 7).orEqualTo("status", 8);
        List<Order> orderList = queryOrderListByExample(example);
        return orderList;
    }

    private List<Order> queryOrderListByExample(Example example) {
        List<OrderStatus> orderStatusList = orderStatusMapper.selectByExample(example);
        List<Long> orderIds = orderStatusList.stream().map(OrderStatus::getOrderId).collect(Collectors.toList());
        List<Order> orderList = orderMapper.selectByIdList(orderIds);
        if (CollectionUtils.isEmpty(orderList)) {
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        orderListAddStateDetailFor(orderList, orderIds, orderStatusList);
        return orderList;
    }

    private void orderListAddStateDetail(List<Order> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        List<Long> orderIds = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
        List<OrderStatus> orderStatuseList = orderStatusMapper.selectByIdList(orderIds);
        orderListAddStateDetailFor(orderList, orderIds, orderStatuseList);
    }

    private void orderListAddStateDetailFor(List<Order> orderList, List<Long> orderIds, List<OrderStatus> orderStatuseList) {
        int flag = 0;

        for (Order orderOne : orderList) {
            int totalNum = 0;
            orderOne.setOrderStatus(orderStatuseList.get(flag));
            //查询订单详情
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderIds.get(flag));
            List<OrderDetail> details = orderDetailMapper.select(detail);
            for (OrderDetail orderDetail : details) {
                totalNum += orderDetail.getNum();
            }
            orderOne.setOrderDetails(details);
            orderOne.setTotalNum(totalNum);
            flag++;
        }
    }

    @Transactional
    public void updateOrderState(Long orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        orderStatus.setStatus(5);
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    public String createPayUrl(Long orderid) {
        //查询订单
        Order order = queryOrderById(orderid);
        //判断订单状态
        Integer status = order.getOrderStatus().getStatus();
        if (status != OrderStatusEnum.UN_PAY.value()) {
            //订单状态异常 不为1
            throw new McException(ExceptionEnum.ORDER_STATUS_ERROR);
        }
        //支付金额
        Long actualPay = /*order.getActualPay()*/ 1L;
        //商品描述
        OrderDetail detail = order.getOrderDetails().get(0);
        String desc = detail.getTitle();
        return payHelper.createOrder(orderid, actualPay, desc);

    }

    public void handleNotify(Map<String, String> result) {
        //1 数据校验
        payHelper.isSuccess(result);

        //2 校验签名
        payHelper.isValidSign(result);

        //3 校验金额
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");
        if (StringUtils.isEmpty(totalFeeStr)) {
            throw new McException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        //3.1 获取结果中的金额
        Long totalFee = Long.valueOf(totalFeeStr);
        //3.2 获取订单中的金额
        Long orderid = Long.valueOf(tradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderid);
        if (totalFee != /*order.getActualPay() */ 1) {
            //金额不符
            throw new McException(ExceptionEnum.INVALID_ORDER_PARAM);
        }

        //4 修改订单状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAYED.value());
        status.setOrderId(orderid);
        status.setPaymentTime(new Date());
        int count = orderStatusMapper.updateByPrimaryKeySelective(status);
        if (count != 1) {
            throw new McException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }
        log.info("[订单回调]，订单支付成功！ ，订单编号:{}", orderid);
    }

    public PayState queryOrderState(Long orderId) {
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        //判断是否支付
        if (status != OrderStatusEnum.UN_PAY.value()) {
            // 如果已经支付，实际上就就已经支付好了
            return PayState.SUCCESS;
        }

        //如果未支付，不一定是未支付，必须去微信查询支付状态
        return payHelper.queryPayState(orderId);
    }


    public PageResult<Order> queryOrderbyPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        List<Order> orderList = queryOrderRight();

        //解析分页结果
        PageInfo<Order> info = new PageInfo<>(orderList);
        return new PageResult<>(info.getTotal(), orderList);

    }

    public PageResult<Order> queryOrderListErrorbyPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        List<Order> orderList = queryOrderError();

        //解析分页结果
        PageInfo<Order> info = new PageInfo<>(orderList);
        return new PageResult<>(info.getTotal(), orderList);
    }

    //**************************************厂家查询正常订单****************************************
    public PageResult<Order> queryUserOrderRightbyPage(Long uid, Integer page, Integer rows, String key) {
        //分页
        PageHelper.startPage(page, rows);

        //查询uid下的所有的正常订单编号
        List<Long> RightIds = userOrderMapper.queryUserRightOrderIds(uid);
        return getOrderPageResultbyOrderidList(RightIds);
    }

    //**************************************厂家查询异常订单订单****************************************
    public PageResult<Order> queryUserOrderErrorbyPage(Long uid, Integer page, Integer rows, String key) {
        //分页
        PageHelper.startPage(page, rows);

        //查询uid下的所有的正常订单编号
        List<Long> Errorids = userOrderMapper.queryUserErrorOrderIds(uid);
        return getOrderPageResultbyOrderidList(Errorids);
    }

    private PageResult<Order> getOrderPageResultbyOrderidList(List<Long> errorids) {
        if (CollectionUtils.isEmpty(errorids)) {
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //查询他们的状态
        List<OrderStatus> orderStatusList = orderStatusMapper.selectByIdList(errorids);
        if (CollectionUtils.isEmpty(orderStatusList)) {
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //查询订单
        List<Order> orderList = orderMapper.selectByIdList(errorids);
        //添加订单细节以及状态码
        if (CollectionUtils.isEmpty(orderList)) {
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        orderListAddStateDetailFor(orderList, errorids, orderStatusList);

        //解析分页结果
        PageInfo<Order> info = new PageInfo<>(orderList);
        return new PageResult<>(info.getTotal(), orderList);
    }

    public PageResult<Order> queryOrderByUidAndState(Long uid, Integer page, Integer rows, Integer status) {
        //分页
        PageHelper.startPage(page, rows);
        List<Long> ids = userOrderMapper.queryUserByUidAndStatus(uid, status);
        List<Order> orderList = orderMapper.selectByIdList(ids);
        orderListAddStateDetail(orderList);
        //解析分页结果
        PageInfo<Order> info = new PageInfo<>(orderList);
        return new PageResult<>(info.getTotal(), orderList);
    }

    public Map<String, String> savePayInfo(Long orderId) {
        Map<String, String> payMap = new HashMap<>();
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null){
            throw new McException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        OrderDetail orderDetail = orderDetailMapper.selectOne(record);
        payMap.put("out_trade_no",orderId.toString());
        payMap.put("product_code","FAST_INSTANT_TRADE_PAY");//产品交易码
        payMap.put("total_amount","0.01");//实际付款金额 0.01测试金额
        payMap.put("subject",orderDetail.getTitle());//商品名称
        return payMap;
    }

    /**
     * 封装消息队列
     * @param cart
     */
    private void sendMessage(Map<Long,Object> cart,String type) {
        try {
            amqpTemplate.convertAndSend("cart." + type, cart);
        } catch (Exception e) {
            log.error("{}订单消息发送异常，商品ID：{}", cart, e);
        }
    }

    public Integer queryOrderStateCount(Integer status) {
        UserInfo user = UserInterceptor.getUser();
        //获取用户所有的订单数据
        Order order = new Order();
        order.setUserId(user.getId());
        List<Order> orderList = orderMapper.select(order);
        if(!CollectionUtils.isEmpty(orderList)){
            List<Long> orderIds = orderList.stream().map(Order :: getOrderId).collect(Collectors.toList());
            Example example = new Example(OrderStatus.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("orderId",orderIds).andEqualTo("status",status);
            return orderStatusMapper.selectCountByExample(example);
        }
        return 0;
    }
/*    public PayState queryOrderAliState(Long orderId) {
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        //判断是否支付
        if (status != OrderStatusEnum.UN_PAY.value()) {
            // 如果已经支付，实际上就就已经支付好了
            return PayState.SUCCESS;
        }
        return AliPayCheckStatus(orderStatus,orderId);
    }*/

  /*  private PayState AliPayCheckStatus(OrderStatus orderStatus,Long orderId) {
        // 调用alipayClient接口，根据out_trade_no查询支付状态
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> mapString = new HashMap<String,Object>();
        mapString.put("out_trade_no",orderId);
        String s = JSON.toJSONString(mapString);
        request.setBizContent(s);
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            orderStatus.setStatus(2);
            orderStatus.setPaymentTime(new Date());
            orderStatusMapper.updateByPrimaryKey(orderStatus);
            return PayState.SUCCESS;
        }else{
            return PayState.NOT_PAY;
        }
    }
*/
}
