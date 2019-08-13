package com.mceil.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mc.sms")
public class SmsProperties {
    String accessKeyId;

    String accessKeySecret;

    String signName;

    String templateCode;

}
