package com.mceil.order.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "mc.ali")
public class AlipayConfig {
    private String alipay_url;

    private String app_id;
    private String app_private_key;

    private String alipay_public_key;

    private String return_payment_url;

    private String notify_payment_url;

    public final static String format="json";
    public final static String charset="utf-8";
    public final static String sign_type="RSA2";
    @Bean
    public AlipayClient alipayClient(){
        AlipayClient alipayClient=new DefaultAlipayClient(alipay_url,app_id,app_private_key,format,charset, alipay_public_key,sign_type );
        return alipayClient;
    }
}
