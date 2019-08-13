package com.mceil.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://192.168.0.123");
        config.addAllowedOrigin("http://192.168.0.123:8081");
        config.addAllowedOrigin("http://192.168.0.108");
        config.addAllowedOrigin("http://localhost:9001");
        config.addAllowedOrigin("http://192.168.0.108:8080");
        config.addAllowedOrigin("http://192.168.0.108:8081");
        config.addAllowedOrigin("http://192.168.0.108:9001");
        config.addAllowedOrigin("http://192.168.0.135");
        config.addAllowedOrigin("http://192.168.0.135:8080");
        config.addAllowedOrigin("http://192.168.0.113");
        config.addAllowedOrigin("http://192.168.0.113:8080");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://api.mceil.com");
        config.addAllowedOrigin("http://www.mceil.com");
        config.addAllowedOrigin("http://admin.mceil.com");
        config.addAllowedOrigin("localhost");
        //2) 是否发送Cookie信息
        config.setAllowCredentials(true);
        //3) 允许的请求方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        // 4）允许的头信息
        config.addAllowedHeader("*");
        // 5) 有效時長
        config.setMaxAge(3600L);

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}