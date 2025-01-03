package com.thirdeye.processer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.thirdeye.processer.interceptors.AuthorizationInterceptor;
import com.thirdeye.processer.utils.AllMicroservicesData;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;
    
    @Autowired
	AllMicroservicesData allMicroservicesData;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/marketviewerprocesser/getlateststock/**");
    }
}

