package com.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Description:  windows本地与inux线上测试OK
 *
 * @author :  Raymond
 * @date :  2019-10-15 15:19
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 重写方法
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploadFiles/**")// 图片镜像（虚拟）路径
                .addResourceLocations("file:F:/upload/");// 图片真实保存路径（windows下）
//                .addResourceLocations("file:/opt/application/upload/201910/");// 图片真实保存路径（linux下）
    }
}
