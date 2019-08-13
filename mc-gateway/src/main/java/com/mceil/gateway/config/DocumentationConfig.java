package com.mceil.gateway.config;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;


@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList<>();
        resources.add(swaggerResource("上传微服务接口", "/api/upload/v2/api-docs", "1.0"));
        resources.add(swaggerResource("商品服务接口", "/api/item/v2/api-docs", "1.0"));
        resources.add(swaggerResource("搜索服务接口", "/api/search/v2/api-docs", "1.0"));
        resources.add(swaggerResource("用户服务接口", "/api/user/v2/api-docs", "1.0"));
        resources.add(swaggerResource("认证服务接口", "/api/auth/v2/api-docs", "1.0"));
        resources.add(swaggerResource("购物车接口", "/api/cart/v2/api-docs", "1.0"));
        resources.add(swaggerResource("订单服务接口", "/api/order/v2/api-docs", "1.0"));
        resources.add(swaggerResource("收藏服务接口", "/api/collects/v2/api-docs", "1.0"));
        resources.add(swaggerResource("Excel服务接口", "/api/excel/v2/api-docs", "1.0"));

//        resources.add(swaggerResource("秒杀服务接口", "/api/seckill/v2/api-docs", "1.0"));
//        resources.add(swaggerResource("评论服务接口", "/api/review/v2/api-docs", "1.0"));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}