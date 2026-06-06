package com.example.controller.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "deepseek")
@Data
public class DeepSeekConfig {
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-chat";
    private Integer maxTokens = 8192;
    private Float temperature = 0.7f;
    private Integer maxResponseTime = 180;
}
