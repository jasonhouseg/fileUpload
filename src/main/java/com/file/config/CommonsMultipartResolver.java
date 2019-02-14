package com.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author jason
 * @date 2019-02-14 12:52
 */
@Configuration
public class CommonsMultipartResolver {

//    @Bean(name = "multipartResolver")
    @Bean
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(20971520);
//        multipartResolver.setMaxInMemorySize(1048576);
        return multipartResolver;
    }
}
