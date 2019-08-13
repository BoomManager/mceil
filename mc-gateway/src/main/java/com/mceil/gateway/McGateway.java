package com.mceil.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableZuulProxy
@SpringCloudApplication
@EnableSwagger2
public class McGateway {
    public static void main(String[] args) {
        SpringApplication.run(McGateway.class);
    }
}
