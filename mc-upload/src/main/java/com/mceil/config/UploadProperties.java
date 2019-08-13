package com.mceil.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
@ConfigurationProperties(prefix = "mc.upload")
@Data
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;
}
