package com.bank.config;

import com.bank.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry r) {
        r.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/login", "/api/login", "/logout", "/static/**", "/webjars/**", "/favicon.ico", "/error");
    }
}
