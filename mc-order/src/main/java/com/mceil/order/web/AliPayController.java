package com.mceil.order.web;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.google.gson.Gson;
import com.mceil.order.config.AlipayConfig;
import com.mceil.order.pojo.AlipayVo;
import com.mceil.order.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("ali")
@EnableConfigurationProperties(AlipayConfig.class)
public class AliPayController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    AlipayClient alipayClient;
    @ApiOperation("阿里支付生成二维码")
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    @ResponseBody
    private String aliPay(@RequestParam("orderId") Long orderId) throws Exception{
        Map<String, String> requestMap = orderService.savePayInfo(orderId);

        //AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getAlipay_url(), alipayConfig.getApp_id(), alipayConfig.getApp_private_key(), "json",AlipayConfig.charset,alipayConfig.getAlipay_public_key());
        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturn_payment_url());
        alipayRequest.setNotifyUrl(alipayConfig.getNotify_payment_url());
        alipayRequest.setBizContent(JSON.toJSONString(requestMap));
        String result="";
        try {
            //调用SDK生成表单
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result; //这里生成一个表单，会自动提交
    }

    /**
     * 支付宝回调签名的校验
     * @param request
     */
    private String verificationOfSignature(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                System.out.println(valueStr);
            }
            map.put(name, valueStr);
        }
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(map,alipayConfig.getAlipay_public_key(),AlipayConfig.charset,AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ("fail");// 验签发生异常,则直接返回失败
        }
        if (signVerified) {
            //处理你的业务逻辑，更新订单状态等
            return ("success");
        } else {
            System.out.println("验证失败,不去更新状态");
            return ("fail");
        }
    }
    @ApiOperation("阿里支付成功后同步回调转支付成功页面")
    @ResponseBody
    @RequestMapping(value = "/alipay/callback/return",method = RequestMethod.GET)
    public ResponseEntity<Void> callBackReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取支付宝验证结果
        String result = verificationOfSignature(request);
        if(result.equals("success")){
            //跳转到支付成功页面
            response.sendRedirect("http://192.168.0.113:8080/shopping/pay");
        }
        return ResponseEntity.ok().build();
    }

  /*  *//**
     * @Title: alipayReturn
     * @Description: 支付宝回调接口
     * @author nelson
     * @param request
     * @param out_trade_no 商户订单号
     * @param trade_no 支付宝交易凭证号
     * @throws AlipayApiException
     * @return String
     * @throws
     *//*
    @GetMapping("return")
    private String alipayReturn(Map<String, String> params, HttpServletRequest request, String out_trade_no, String trade_no, String total_amount)
            throws AlipayApiException {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                System.out.println(valueStr);
            }
            map.put(name, valueStr);
        }
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(map, alipayConfig.getAlipay_public_key(), charset, sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ("fail");// 验签发生异常,则直接返回失败
        }
        if (signVerified) {
            return ("success");
        } else {
            System.out.println("验证失败,不去更新状态");
            return ("fail");
        }
    }
*/
}

