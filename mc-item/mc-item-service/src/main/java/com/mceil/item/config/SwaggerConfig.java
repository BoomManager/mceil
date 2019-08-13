package com.mceil.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                //.host("http://api.mceil.com")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mceil.item.web"))
                .paths(PathSelectors.any())
//                .securitySchemes(securitySchemes())
//                .securityContexts(securityContexts())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("商品微服务")
                .description("商品微服务接口文档")
                .version("1.0")
                .build();
    }


//
//    private List<ApiKey> securitySchemes() {
//        //设置请求头信息
//        List<ApiKey> result = new ArrayList<>();
//        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
//        result.add(apiKey);
//        return result;
//    }
//
//    private List<SecurityContext> securityContexts() {
//        //设置需要登录认证的路径
//        List<SecurityContext> result = new ArrayList<>();
//        result.add(getContextByPath("/brand/.*"));
//        return result;
//    }
//
//    private SecurityContext getContextByPath(String pathRegex){
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(PathSelectors.regex(pathRegex))
//                .build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        List<SecurityReference> result = new ArrayList<>();
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        result.add(new SecurityReference("Authorization", authorizationScopes));
//        return result;
//    }
}