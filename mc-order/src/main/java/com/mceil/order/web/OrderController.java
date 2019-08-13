package com.mceil.order.web;

import com.mceil.common.vo.PageResult;
import com.mceil.order.dto.OrderDTO;
import com.mceil.order.dto.PageBean;
import com.mceil.order.pojo.Order;
import com.mceil.order.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@Api("订单服务接口")
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "创建订单接口, 返回订单编号", notes = "创建订单")
    @ApiResponses({
            @ApiResponse(code = 200, message = "创建的订单编号"),
            @ApiResponse(code = 500, message = "服务异常")
    })
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        //创建订单
        //System.out.println("订单编号:"+orderService.createOrder(orderDTO));
        Long orderId = orderService.createOrder(orderDTO);

     return ResponseEntity.ok(orderId);
    }

    /**
     *
     * 根据id查询订单
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据订单id查询订单", notes = "查询订单")
    @ApiResponses({
            @ApiResponse(code = 200, message = "根据订单编号所查询的订单"),
            @ApiResponse(code = 500, message = "服务异常")
    })
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }
    /**
     *
     * 根据订单状态查询订单个数
     * @param id
     * @return
     */
    @GetMapping("query/state")
    @ApiOperation(value = "根据订单状态查询订单个数", notes = "根据订单状态查询订单个数")
    @ApiResponses({
            @ApiResponse(code = 200, message = "根据订单状态查询订单个数"),
            @ApiResponse(code = 500, message = "服务异常")
    })
    public ResponseEntity<Integer> queryOrderById(@RequestParam("status") Integer status){
        return ResponseEntity.ok(orderService.queryOrderStateCount(status));
    }

    @GetMapping("orderList")
    public ResponseEntity<PageResult<Order>> queryOrderbyPage(
            @RequestParam(value = "page" ,defaultValue = "1") Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy" ,required = false) String sortBy,
            @RequestParam(value = "desc" ,defaultValue = "false") Boolean desc,
            @RequestParam(value = "key" , required = false) String key){
        return ResponseEntity.ok(orderService.queryOrderbyPage(page, rows, sortBy, desc, key));
    }
    @GetMapping("orderErrorList")
    public ResponseEntity<PageResult<Order>> queryOrderListErrorbyPage(
            @RequestParam(value = "page" ,defaultValue = "1") Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy" ,required = false) String sortBy,
            @RequestParam(value = "desc" ,defaultValue = "false") Boolean desc,
            @RequestParam(value = "key" , required = false) String key
            ){
        return ResponseEntity.ok(orderService.queryOrderListErrorbyPage(page, rows, sortBy, desc, key));
    }

    @ApiOperation(value = "更新订单状态", notes = "更新订单状态")
    @PostMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderState(@PathVariable Long orderId){
        orderService.updateOrderState(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 创建支付链接(根据订单号来生成支付url)
     * @param id
     * @return
     */
    @GetMapping("/url/{id}")
    @ApiOperation(value = "创建支付链接", notes = "创建微信支付链接(根据订单号来生成支付url)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "根据订单编号生成的微信支付地址"),
            @ApiResponse(code = 400, message = "生成链接失败"),
            @ApiResponse(code = 500, message = "服务器异常")
    })
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.createPayUrl(id));
    }

    /**
     *查询支付状态
     * @param orderId
     * @return
     */
    @GetMapping("/state/{id}")
    @ApiOperation(value = "查询扫码支付的付款状态", notes = "查询付款状态")
    @ApiImplicitParam(name = "id", value = "订单编号" ,type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "0,未查询到支付信息 1, 支付成功 2, 支付失败"),
            @ApiResponse(code = 500, message = "服务器异常")
    })
    public ResponseEntity<Integer> queryOrderState(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(orderService.queryOrderState(orderId).getValue());
    }

    //****************************厂家**************************************
    @GetMapping("user/orderList/right")
    public ResponseEntity<PageResult<Order>> queryUserOrderRightbyPage(
            @RequestParam(value = "uid", required = true) Long uid,
            @RequestParam(value = "page" ,defaultValue = "1") Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows,
            @RequestParam(value = "key" , required = false) String key){
        return ResponseEntity.ok(orderService.queryUserOrderRightbyPage(uid ,page, rows, key));
    }
    @GetMapping("user/orderList/error")
    public ResponseEntity<PageResult<Order>> queryUserOrderErrorbyPage(
            @RequestParam(value = "uid", required = true) Long uid,
            @RequestParam(value = "page" ,defaultValue = "1") Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows,
            @RequestParam(value = "key" , required = false) String key){
        return ResponseEntity.ok(orderService.queryUserOrderErrorbyPage(uid ,page, rows, key));
    }
    //*************************代销商*********************************************
    @ApiOperation(value = "获取当前用户订单", notes = "获取当前用户订单")
    @GetMapping("user")
    public ResponseEntity<PageResult<Order>> queryOrderByUid(
            @RequestParam(value = "page" ,defaultValue = "1") Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows,
            @RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value = "status",required = false) Integer status,
            @RequestParam(value = "orderId" , required = false) Long orderId){
        return ResponseEntity.ok(orderService.queryOrderByUid(page, rows,keyword,status,orderId));
    }

    @GetMapping("user/state")
    public ResponseEntity<PageResult<Order>> queryOrderByUidAndState(
            @RequestParam(value = "uid", required = true) Long uid,
            @RequestParam(value = "page" ,defaultValue = "1") Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows,
            @RequestParam(value = "status", required = true) Integer status
    ){
        return ResponseEntity.ok(orderService.queryOrderByUidAndState(uid, page, rows, status));
    }
}
