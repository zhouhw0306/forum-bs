package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 资源路径映射配置
 * @author 24668
 */
@Configuration
public class MyPicConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatarImages/**").addResourceLocations("file:"+System.getProperty("user.dir")+"/forum-server/data/avatarImages/");
        registry.addResourceHandler("/articleFile/**").addResourceLocations("file:"+System.getProperty("user.dir")+"/forum-server/data/articleFile/");
    }
}
